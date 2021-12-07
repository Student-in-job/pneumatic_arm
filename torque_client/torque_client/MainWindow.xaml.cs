using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Windows.Threading;
using System.IO;
using System.IO.Ports;
using System.Text.RegularExpressions;
using System.Net;
using System.ComponentModel;
using System.Threading;
using CsvHelper;

namespace torque_client
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private DispatcherTimer timer = new DispatcherTimer();
        public static string comPortName = "";
        private SerialPort comPort = new SerialPort();
        bool Connected = false;
        private string filename = "data.csv";
        private string url = "http://localhost:8000/api/v1/";
        int[] columns = { 0, 0, 0, 0 };
        List<string> TimeControllerList = new List<string>();
        List<string> ForceControllerList = new List<string>();
        List<string> timeValues = new List<string>();
        BackgroundWorker worker;

        public MainWindow()
        {
            InitializeComponent();
            timer.Tick += Timer_tick;
            timer.Interval = new TimeSpan(0, 0, 5);
            timer.Start();
            string[] ports = SerialPort.GetPortNames();
            Array.Reverse(ports);
            this.update_entries(ports);
            worker = new BackgroundWorker();
            worker.WorkerReportsProgress = true;
            worker.DoWork += worker_DoWork;
            worker.ProgressChanged += worker_ProgressChanged;
        }

        public void Timer_tick(object Sender, EventArgs e)
        {
            string[] ports = SerialPort.GetPortNames();
            Array.Reverse(ports);
            this.update_entries(ports);
        }
                private void txt_NumbersOnly(object sender, TextCompositionEventArgs e)
        {
            Regex regex = new Regex("[^0-9]+");
            e.Handled = regex.IsMatch(e.Text);
        }
        /// <summary>
        /// Updating connected COM ports
        /// </summary>
        /// <param name="portNames"></param>
        private void update_entries(string[] portNames)
        {
            List<string> availablePorts = new List<string>();
            List<string> unavailablePorts = new List<string>();
            int position = 0;
            foreach (string item in this.cmbPort.Items)
            {
                if (portNames.Contains(item))
                {
                    availablePorts.Add(item);
                }
                else
                {
                    unavailablePorts.Add(item);
                }
                position++;
            }
            unavailablePorts.Reverse();
            // Remove not existing ports
            foreach (string item in unavailablePorts)
            {
                this.cmbPort.Items.Remove(item);
            }
            // Add new ports
            foreach (string portName in portNames)
            {
                if (!availablePorts.Contains(portName))
                {
                    //ComboBoxItem newPort = new ComboBoxItem();
                    this.cmbPort.Items.Add(portName);
                }
            }
        }

        private void btnConnect_Click(object sender, RoutedEventArgs e)
        {
            if (!Connected)
            {
                comPort.PortName = comPortName;
                comPort.BaudRate = int.Parse(this.txtBaudRate.Text);
                comPort.Open();
                //this.txtOutput.Text += string.Format("Com port: {0} is connected\n", comPortName);
                this.btnConnectText.Text = "Disconnect";
            }
            else
            {
                comPort.Close();
                this.btnConnectText.Text = "Connect";
            }
        }

        protected class Time_Force
        {
            public string Time { get; set; }
            public string Force { get; set; }
        }

        private void btnSave_Click(object sender, RoutedEventArgs e)
        {
            List<Time_Force> records = new List<Time_Force>();
            for (int index = 0; index < this.TimeControllerList.Count; index++)
            {
                TextBox txtTime = (TextBox)this.controlContainer.FindName(this.TimeControllerList[index]);
                TextBox txtForce = (TextBox)this.controlContainer.FindName(this.ForceControllerList[index]);
                Time_Force tf = new Time_Force{ Time = txtTime.Text, Force = txtForce.Text};
                records.Add(tf);
            }
            string directory = Directory.GetParent(AppDomain.CurrentDomain.BaseDirectory).Parent.Parent.Parent.FullName;
            string time = DateTime.Now.ToString("yyyy-MM-dd_HH_mm");
            using (var writer = new StreamWriter(directory + "\\data_" + time + ".csv"))
            using (var csv = new CsvWriter(writer, System.Globalization.CultureInfo.InvariantCulture))
            {
                csv.WriteRecords(records);
            }
            MessageBox.Show("Data has been saved");
        }

        private void InitTextboxes(int start, int step, int end)
        {
            int count = (end - start) / step + 1;
            foreach (string name in this.TimeControllerList)
            {
                UnregisterName(name);
            }
            foreach (string name in this.ForceControllerList)
            {
                UnregisterName(name);
            }
            this.TimeControllerList.Clear();
            this.ForceControllerList.Clear();
            int top = 10;
            int left = 10;
            int increment_left = 150;
            int increment_top = 30;
            int width = 50;
            int column = 0;
            int row = 1;
            this.Calc_columns(count);
            this.controlContainer.Children.Clear();
            for (int i = 1; i <= count; i++)
            {
                TextBox txtTime = new TextBox();
                txtTime.Text = start.ToString();
                txtTime.Width = width;
                txtTime.FontSize = 14;
                Canvas.SetLeft(txtTime, column * increment_left + left);
                Canvas.SetTop(txtTime, top);
                txtTime.Name = "txtTime" + i.ToString();
                txtTime.IsReadOnly = true;
                RegisterName(txtTime.Name, txtTime);
                this.controlContainer.Children.Add(txtTime);
                this.TimeControllerList.Add(txtTime.Name);
                TextBox txtForce = new TextBox();
                txtForce.Width = width;
                txtForce.FontSize = 14;
                //float f = (float)start / 121;
                //txtForce.Text = f.ToString();
                Canvas.SetLeft(txtForce, column * increment_left + 70);
                Canvas.SetTop(txtForce, top);
                txtForce.Name = "txtForce" + i.ToString();
                RegisterName(txtForce.Name, txtForce);
                this.ForceControllerList.Add(txtForce.Name);
                this.controlContainer.Children.Add(txtForce);
                if (row == columns[column])
                {
                    top = 10;
                    column++;
                    row = 1;
                }
                else
                {
                    top += increment_top;
                    row++;
                }
                start += step;
                
            }
            int container_height = columns[0] * increment_top + 60;
            if (container_height > controlContainer.Height)
            {
                controlContainer.Height = container_height;
            }
        }

        private void btnGenerate_Click(object sender, RoutedEventArgs e)
        {
            InitTextboxes(int.Parse(this.txtStart.Text), int.Parse(this.txtStep.Text), int.Parse(this.txtEnd.Text));
            this.btnStart.IsEnabled = true;
        }

        private void Calc_columns(int count)
        {
            int per_column = (int)count / 4;
            for (int column_index = 0; column_index < 4; column_index++)
            {
                this.columns[column_index] = per_column;
            }
            for (int column_index = 0; column_index < count % 4; column_index++)
            {
                this.columns[column_index] += 1;
            }
        }

        private void Send_Post(string value = "2000", bool IsGet = false)
        {
            string responseText = string.Empty;
            HttpWebRequest request;
            if (IsGet)
            {

                request = (HttpWebRequest)WebRequest.Create(url + "cancel/");
                request.Method = "GET";
                request.Timeout = 30 * 1000;
            }
            else
            {
                request = (HttpWebRequest)WebRequest.Create(url + "message/");
                request.Method = "POST";
                request.Timeout = 30 * 1000;
                request.ContentType = "application/x-www-form-urlencoded";
                string postData = "message=" + value;
                byte[] byteArray = Encoding.UTF8.GetBytes(postData);
                request.ContentLength = byteArray.Length;
                // Get the request stream.
                Stream dataStream = request.GetRequestStream();
                // Write the data to the request stream.
                dataStream.Write(byteArray, 0, byteArray.Length);
                // Close the Stream object.
                dataStream.Close();
            }
            using (HttpWebResponse resp = (HttpWebResponse)request.GetResponse())
            {
                HttpStatusCode status = resp.StatusCode;
                if (!(HttpStatusCode.OK == status))
                    MessageBox.Show("Error in responce status: " + status.ToString(), "Error");
                else
                    this.sendAsyncTextToOutput("Data has been sent");
            }
        }

        void worker_DoWork(object sender, DoWorkEventArgs e)
        {
            this.sendAsyncTextToOutput("Running");

            int progress = 0;
            int step = 100 / this.TimeControllerList.Count;
            this.unblock_control();
            foreach (string value in timeValues)
            {
                this.Send_Post(value);
                Thread.Sleep(2000);
                this.Send_Post(IsGet: true);
                Thread.Sleep(3000);
                progress += step;
                (sender as BackgroundWorker).ReportProgress(progress);
            }
            this.unblock_control(true);
            //for (int i = 0; i <= 100; i++)
            //{
            //    (sender as BackgroundWorker).ReportProgress(i);
            //    Thread.Sleep(100);
            //}
            this.sendAsyncTextToOutput("Finished");
        }

        void worker_ProgressChanged(object sender, ProgressChangedEventArgs e)
        {
            barProgres.Value = e.ProgressPercentage;
        }

        private void sendAsyncTextToOutput(string message)
        {
            this.Dispatcher.Invoke(() =>
            {
                lblstatus.Text = message;
            });
        }

        private void unblock_control(bool enabled = false)
        {
            this.Dispatcher.Invoke(() =>
            {
                this.btnSend.IsEnabled = enabled;
                this.btnGenerate.IsEnabled = enabled;
                this.btnSave.IsEnabled = enabled;
                this.btnStart.IsEnabled = false;
            });
        }

        private void btnStart_Click(object sender, RoutedEventArgs e)
        {
            this.timeValues.Clear();
            foreach (string name in this.TimeControllerList)
            {
                TextBox txtTime = (TextBox)this.controlContainer.FindName(name);
                this.timeValues.Add(txtTime.Text);
            }

            worker.RunWorkerAsync();
        }

        private void btnSend_Click(object sender, RoutedEventArgs e)
        {
            this.Send_Post(this.txtOneVal.Text);
            Thread.Sleep(2000);
            this.Send_Post(IsGet:true);
        }
    }
}

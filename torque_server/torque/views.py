import math
import time


import serial
from rest_framework import generics, status
from rest_framework.permissions import AllowAny
from rest_framework.response import Response

from torque.serializers import ReceiveMessageSerializer, NewtonsSerializer
from torque.settings import COM_PORT, BAUD_RATE


class ReceiveView(generics.GenericAPIView):
    serializer_class = ReceiveMessageSerializer
    permission_classes = [AllowAny, ]

    def post(self, request):
        serializer = self.serializer_class(data=request.data)
        serializer.is_valid(raise_exception=True)

        message = serializer.data.get('message')
        print(message_from_int(int(message)))
        ser = serial.Serial(COM_PORT)
        time.sleep(2)
        ser.baudrate = BAUD_RATE
        ser.write(str.encode(message_from_int(int(message))))
        ser.close()
        return Response({"result": "ok"}, status=status.HTTP_200_OK)


class CancelView(generics.GenericAPIView):
    def get(self, request):
        message = '#00000'
        print(message)
        ser = serial.Serial(COM_PORT)
        time.sleep(2)
        ser.baudrate = BAUD_RATE
        ser.write(str.encode(message))
        ser.close()
        return Response({"result": "ok"}, status=status.HTTP_200_OK)


class ForceView(generics.GenericAPIView):
    serializer_class = NewtonsSerializer
    permission_classes = [AllowAny, ]

    def post(self, request):
        serializer = self.serializer_class(data=request.data)
        serializer.is_valid(raise_exception=True)

        newtons = serializer.data.get('message')
        print(newtons)
        print(type(newtons))
        if newtons <= 11.5:
            expected_time = linear(newtons)
        else:
            expected_time = exponent(float(newtons))

        ser = serial.Serial(COM_PORT)
        time.sleep(2)
        ser.baudrate = BAUD_RATE
        print(create_message(expected_time))
        ser.write(str.encode(create_message(expected_time)))
        ser.close()
        return Response({"result": "ok"}, status=status.HTTP_200_OK)

class TorqueView(generics.GenericAPIView):
    serializer_class = NewtonsSerializer
    permission_classes = [AllowAny, ]

    def post(self, request):
        serializer = self.serializer_class(data=request.data)
        serializer.is_valid(raise_exception=True)

        torque = serializer.data.get('message')
        print(torque)
        print(torque_to_force(float(torque)))
        expected_time = linear(torque_to_force(float(torque)))

        ser = serial.Serial(COM_PORT)
        time.sleep(2)
        ser.baudrate = BAUD_RATE
        print(create_message(expected_time))
        ser.write(str.encode(create_message(expected_time)))
        ser.close()
        return Response({"result": "ok"}, status=status.HTTP_200_OK)

class PressureView(generics.GenericAPIView):
    serializer_class = NewtonsSerializer
    permission_classes = [AllowAny, ]

    def post(self, request):
        serializer = self.serializer_class(data=request.data)
        serializer.is_valid(raise_exception=True)

        pressure = serializer.data.get('message')
        print(math.floor(pressure * 100))
        
        ser = serial.Serial(COM_PORT)
        time.sleep(2)
        ser.baudrate = BAUD_RATE

        print(create_message2(pressure))
        ser.write(str.encode(create_message2(pressure)))
        ser.close()
        return Response({"result": "ok"}, status=status.HTTP_200_OK)

class TorquePressureView(generics.GenericAPIView):
    serializer_class = NewtonsSerializer
    permission_classes = [AllowAny, ]

    def post(self, request):
        serializer = self.serializer_class(data=request.data)
        serializer.is_valid(raise_exception=True)

        torque = serializer.data.get('message')
        print(torque)
        print(torque_to_force(float(torque)))
        expected_psi = linear2(torque_to_force(float(torque)))
        print(expected_psi)

        ser = serial.Serial(COM_PORT)
        time.sleep(2)
        ser.baudrate = BAUD_RATE
        print(create_message2(expected_psi))
        ser.write(str.encode(create_message2(expected_psi)))
        ser.close()
        return Response({"result": "ok"}, status=status.HTTP_200_OK)

def linear(newtons):
    #a = 8.813267581
    #b = 1.369069361
    a = 11.1762
    b = 24.3214
    return a * newtons + b

def linear2(newtons):
    a = 0.3645
    b = 5.7736
    return a * newtons + b

def exponent(newtons):
    a = 0.048080292247692
    b = 0.818533599837385
    return a * math.exp(b * newtons)


def create_message(exponent_value):
    exponent_value = math.floor(exponent_value)
    return message_from_int(exponent_value)

def create_message2(exponent_value):
    exponent_value = math.floor(exponent_value * 100)
    return message_from_int2(exponent_value)

def message_from_int(value):
    if 999 < value <= 9999:
        message = '#1%s' % value
    elif 99 < value <= 1000:
        message = '#10%s' % value
    elif 9 < value <= 100:
        message = '#100%s' % value
    else:
        message = '#1000%s' % value
    return message

def message_from_int2(value):
    if 999 < value <= 9999:
        message = '#%s' % value
    elif 99 < value <= 1000:
        message = '#0%s' % value
    elif 9 < value <= 100:
        message = '#00%s' % value
    else:
        message = '#000%s' % value
    return message

def torque_to_force(torque):
    angle = 1.27687
    l = 0.2
    f = torque / (math.sin(angle) * l)
    return f

def force_to_torque(force):
    angle = 1.27687
    l = 0.2
    t = force * math.sin(angle) * l
    return t
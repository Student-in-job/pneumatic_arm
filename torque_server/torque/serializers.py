from rest_framework import serializers


class ReceiveMessageSerializer(serializers.Serializer):
    message = serializers.CharField()


class NewtonsSerializer(serializers.Serializer):
    message = serializers.FloatField()
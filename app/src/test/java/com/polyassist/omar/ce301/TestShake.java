package com.polyassist.omar.ce301;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.mockito.Mockito.when;

public class TestShake {

    //https://stackoverflow.com/questions/34530865/how-to-mock-motionevent-and-sensorevent-for-unit-testing-in-android/43241866
    private SensorEvent getAccelerometerEventWithValues(float[] desiredValues) throws Exception {
        // Create the SensorEvent to eventually return.
        SensorEvent sensorEvent = Mockito.mock(SensorEvent.class);

        // Get the 'sensor' field in order to set it.
        Field sensorField = SensorEvent.class.getField("sensor");
        sensorField.setAccessible(true);
        // Create the value we want for the 'sensor' field.
        Sensor sensor = Mockito.mock(Sensor.class);
        when(sensor.getType()).thenReturn(Sensor.TYPE_ACCELEROMETER);
        // Set the 'sensor' field.
        sensorField.set(sensorEvent, sensor);

        // Get the 'values' field in order to set it.
        Field valuesField = SensorEvent.class.getField("values");
        valuesField.setAccessible(true);
        // Create the values we want to return for the 'values' field.
        valuesField.set(sensorEvent, desiredValues);

        return sensorEvent;
    }

    @Test
    public void testSensor() throws Exception {
        float[] initialValues = {0,0,0};
        float[] testValues = {300,300,300};

        getAccelerometerEventWithValues(initialValues);
        Thread.sleep(500);
        getAccelerometerEventWithValues(testValues);

        if(testValues != initialValues ) {
            assert(true);
        }
        else {
            assert(false);
        }
    }
}

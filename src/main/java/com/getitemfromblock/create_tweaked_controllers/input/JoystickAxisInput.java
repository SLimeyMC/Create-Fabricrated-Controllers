package com.getitemfromblock.create_tweaked_controllers.input;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class JoystickAxisInput implements GenericInput
{
    private int axisID = -1;
    private float minBound = 0.0f;
    private float maxBound = 1.0f;

    public JoystickAxisInput(int axisID)
    {
        this.axisID = axisID;
    }

    public JoystickAxisInput()
    {
    }

    public JoystickAxisInput(int axisID, float min, float max)
    {
        this.axisID = axisID;
        this.minBound = min;
        this.maxBound = max;
    }

    @Override
    public boolean GetButtonValue()
    {
        return GetAxisValue() >= 0.5f;
    }

    @Override
    public float GetAxisValue()
    {
        float v = (JoystickInputs.GetAxis(axisID) - minBound) / (maxBound - minBound);
        if (v < 0) v = 0;
        if (v > 1) v = 1;
        return v;
    }

    @Override
    public String GetDisplayName()
    {
        return "Joystick axis " + axisID;
    }

    @Override
    public boolean IsInputValid()
    {
        return axisID < JoystickInputs.GetAxisCount() && axisID >= 0 && minBound != maxBound;
    }

    @Override
    public void Serialize(DataOutputStream buf) throws IOException
    {
        buf.writeFloat(minBound);
        buf.writeFloat(maxBound);
        buf.writeInt(axisID);
    }

    @Override
    public void Deserialize(DataInputStream buf) throws IOException
    {
        minBound = buf.readFloat();
        maxBound = buf.readFloat();
        axisID = buf.readInt();
    }

    @Override
    public InputType GetType()
    {
        return InputType.JOYSTICK_AXIS;
    }

    @Override
    public int GetValue()
    {
        return axisID;
    }
    
}
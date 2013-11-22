package intermediate;

import java.util.ArrayList;

public class ActivationRecord extends ArrayList<ActivationRecordEntry>
{
    private ActivationRecord activationRecord;

    public ActivationRecord()
    {
        this.activationRecord = null;
    }

    public void setActivationRecord(ActivationRecord activationRecord)
    {
        this.activationRecord = activationRecord;
    }
}



package intermediate;

public class ActivationRecordEntry {

        private String name;
        private Object value;

        public ActivationRecordEntry(String name, Object value)
        {
            this.name = name;
            this.value = value;
        }

        public void setValue(Object value)
        {
            this.value = value;
        }

        public Object getValue()
        {
            return value;
        }

}

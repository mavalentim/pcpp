package main.java.exercises01;

public class MyClass {
    private Object aiai = new Object();
    private int sharedValue = 42;

    // this method of thisclass will work as a reader
    public int getSharedValue() {
        synchronized (aiai) {
            return sharedValue;
        }
    }

    // this method of thisclass will work as a writer
    public void setSharedValue(int sharedValue) {
        synchronized (aiai) {
            this.sharedValue = sharedValue;
        }
    }

    public static void main(String[] args) {
        MyClass myInstance = new MyClass();

        Thread reader = new Thread() {
            public void run() {
                System.out.println("The shared value is: " + myInstance.getSharedValue());
            }
        };

        // and a thread that will overqwite the sharedValue
        Thread writer = new Thread() {
            public void run() {
                myInstance.setSharedValue(89);
                System.out.println("The shared value is: " + myInstance.getSharedValue());
            }
        };

        reader.start();
        writer.start();
        try {
            reader.join();
            writer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    // now write a thread that will read the sharedValue

}

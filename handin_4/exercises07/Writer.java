package exercises07;

public class Writer extends Holders {
    private Thread current;

    public Writer(Thread thread) {
        this.current = thread;
    }

    // created .equals just to guarantee
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((current == null) ? 0 : current.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Writer other = (Writer) obj;
        if (current == null) {
            if (other.current != null)
                return false;
        } else if (!current.equals(other.current))
            return false;
        return true;
    }

}

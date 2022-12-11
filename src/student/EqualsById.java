package student;

import java.util.Objects;

/**
 * Subclasses of this class are compared for equality by their id.
 */
public abstract class EqualsById {
    /**
     * Return the id of the object.
     * @return the id
     */
    public abstract long getId();

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EqualsById other)) {
            return false;
        }
        return Objects.equals(getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}

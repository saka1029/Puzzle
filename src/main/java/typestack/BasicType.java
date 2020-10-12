package typestack;

import java.util.Objects;

public class BasicType implements Type {

        final Class<?> type;

        public BasicType(Class<?> type) {
            Objects.requireNonNull(type, "type");
            this.type = type;
        }

        @Override
        public boolean isAssignableFrom(Type other) {
            return other.getClass() == getClass() ?
                type.isAssignableFrom(((BasicType)other).type) : false;
        }

        @Override
        public int hashCode() {
            return type.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            BasicType other = (BasicType) obj;
            return other.type == type;
        }

        @Override
        public String toString() {
            return type.getSimpleName();
        }

}

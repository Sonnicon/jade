package sonnicon.jade.util;

public interface ICopyable {
    default Object copy() {
        try {
            return getClass().newInstance();
        } catch (Exception ex) {
            throw new InstantiationError();
        }
    }
}

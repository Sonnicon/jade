package sonnicon.jade.game;

public enum EntitySize {
    // walls, can't carry these
    colossal(Integer.MAX_VALUE),
    // small tree
    huge(1500),
    // microwave
    large(500),
    // 2 litres
    medium(100),
    // book
    compact(25),
    // pen
    small(5),
    // pill
    tiny(1);

    public final int value;

    EntitySize(int value) {
        this.value = value;
    }
}

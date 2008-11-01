package org.catacombae.hfsexplorer;

public class ObjectContainer<A> {

    public volatile A o;

    public ObjectContainer(A o) {
        super();
        this.o = o;
    }
}

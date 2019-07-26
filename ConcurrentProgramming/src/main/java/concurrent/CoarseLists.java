package concurrent;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Wrapper class for two lock-based concurrent list implementations.
 */
public final class CoarseLists {
    /**
     * An implementation of the ListSet interface that uses Java locks to
     * protect against concurrent accesses.
     */
    public static final class CoarseList extends ListSet {
        ReentrantLock lock = new ReentrantLock();

        /**
         * Default constructor.
         */
        public CoarseList() {
            super();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        boolean add(final Integer object) {
            lock.lock();
            Entry pred = this.head;
            Entry curr = pred.next;

            while (curr.object.compareTo(object) < 0) {
                pred = curr;
                curr = curr.next;
            }

            if (object.equals(curr.object)) {
                lock.unlock();
                return false;
            } else {
                final Entry entry = new Entry(object);
                entry.next = curr;
                pred.next = entry;
                lock.unlock();
                return true;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        boolean remove(final Integer object) {
            lock.lock();
            Entry pred = this.head;
            Entry curr = pred.next;

            while (curr.object.compareTo(object) < 0) {
                pred = curr;
                curr = curr.next;
            }

            if (object.equals(curr.object)) {
                pred.next = curr.next;
                lock.unlock();
                return true;
            } else {
                lock.unlock();
                return false;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        boolean contains(final Integer object) {
            lock.lock();
            Entry pred = this.head;
            Entry curr = pred.next;

            while (curr.object.compareTo(object) < 0) {
                pred = curr;
                curr = curr.next;
            }
            lock.unlock();
            return object.equals(curr.object);
        }
    }

    /**
     * An implementation of the ListSet interface that uses Java read-write
     * locks to protect against concurrent accesses.
     */
    public static final class RWCoarseList extends ListSet {
        /*
         * TODO Declare a read-write lock for this class to be used in
         * implementing the concurrent add, remove, and contains methods below.
         */
        ReentrantReadWriteLock readWritelock = new ReentrantReadWriteLock();
        /**
         * Default constructor.
         */
        public RWCoarseList() {
            super();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        boolean add(final Integer object) {
            readWritelock.writeLock().lock();
            try {
                Entry pred = this.head;
                Entry curr = pred.next;

                while (curr.object.compareTo(object) < 0) {
                    pred = curr;
                    curr = curr.next;
                }

                if (object.equals(curr.object)) {
                    return false;
                } else {
                    final Entry entry = new Entry(object);
                    entry.next = curr;
                    pred.next = entry;
                    return true;
                }
            }
            finally {
                readWritelock.writeLock().unlock();
            }

        }

        /**
         * {@inheritDoc}
         */
        @Override
        boolean remove(final Integer object) {
            readWritelock.writeLock().lock();
            try {
                Entry pred = this.head;
                Entry curr = pred.next;

                while (curr.object.compareTo(object) < 0) {
                    pred = curr;
                    curr = curr.next;
                }

                if (object.equals(curr.object)) {
                    pred.next = curr.next;
                    return true;
                } else {
                    return false;
                }
            }
            finally {
                readWritelock.writeLock().unlock();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        boolean contains(final Integer object) {
            readWritelock.readLock().lock();
            try {
                Entry pred = this.head;
                Entry curr = pred.next;

                while (curr.object.compareTo(object) < 0) {
                    pred = curr;
                    curr = curr.next;
                }
                return object.equals(curr.object);
            }
            finally {
                readWritelock.readLock().unlock();
            }
        }
    }
}

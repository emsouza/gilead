package net.sf.gilead.test.domain.misc;

import java.util.Comparator;

/**
 * Test class for enum (from drenda81)
 * 
 * @author bruno.marchesson
 */
public class Style {

    /**
     * Horizontal alignment enumeration.
     */
    public enum HorizontalAlignment {
        LEFT,
        CENTER,
        RIGHT
    }

    /**
     * Vertical alignment enumerations.
     */
    public enum VerticalAlignment {
        TOP,
        MIDDLE,
        BOTTOM;
    }

    /**
     * Scroll enumeration.
     */
    public enum Scroll {
        AUTO("auto"),
        ALWAYS("scroll"),
        NONE("hidden");
        private final String value;

        private Scroll(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    /**
     * Sort direction enum.
     */
    public enum SortDir {

        NONE {
            @Override
            @SuppressWarnings({ "rawtypes", "unchecked" })
            public Comparator comparator(Comparator c) {
                return c;
            }
        },

        ASC {
            @Override
            public <X> Comparator<X> comparator(final Comparator<X> c) {
                return new Comparator<X>() {
                    @Override
                    public int compare(X o1, X o2) {
                        return c.compare(o1, o2);
                    }
                };
            }
        },

        DESC {
            @Override
            public <X> Comparator<X> comparator(final Comparator<X> c) {
                return new Comparator<X>() {
                    @Override
                    public int compare(X o1, X o2) {
                        return c.compare(o2, o1);
                    }
                };
            }
        };

        public static SortDir toggle(SortDir sortDir) {
            return (sortDir == ASC) ? DESC : ASC;
        }

        /**
         * An example of how to use this : List<Something> list = ... Collections.sort(list, SortDir.ASC.comparator(new
         * Comparator() { public int compare(Object o1, Object o2) { return ... } });
         *
         * @return a Comparator that wraps the specific comparator that orders the results acording to the sort
         *         direction
         */
        public abstract <X> Comparator<X> comparator(Comparator<X> c);
    }

    /**
     * Layout out regions.
     */
    public enum LayoutRegion {
        NORTH,
        EAST,
        SOUTH,
        WEST,
        CENTER;
    }

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT;
    }

    /**
     * Scroll direction enum.
     */
    public enum ScrollDir {
        VERTICAL,
        HORIZONTAL;
    }

    /**
     * Selection mode enum.
     */
    public enum SelectionMode {
        SINGLE,
        SIMPLE,
        MULTI;
    }

    /**
     * Orientation enum.
     */
    public enum Orientation {
        VERTICAL,
        HORIZONTAL;
    }

    /**
     * A constant known to be zero (0).
     */
    public static final int NONE = 0;

    /**
     * Indicates that a default value should be used (value is -1).
     */
    public static final int DEFAULT = -1;

    /**
     * Constant for marking a string as undefined rather than null.
     */
    public static final String UNDEFINED = "undefined";

    // ----
    // Attribute
    // ----
    /**
     * Sort direction
     */
    private SortDir sortDir = SortDir.ASC;

    /**
     * @return the sort direction
     */
    public SortDir getSortDir() {
        return sortDir;
    }

    /**
     * @param sortDir the sort direction to set
     */
    public void setSortDir(SortDir sortDir) {
        this.sortDir = sortDir;
    }

}
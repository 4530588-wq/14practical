import java.text.DecimalFormat;
import java.util.*;

public class TimeMethods {

    public static final int N = 1 << 20;
    public static final int USE = 950000;
    public static final int REPETITIONS = 30;

    /* ================= OPEN HASH (LINEAR PROBING) ================= */

    static class OpenHash {

        static class Entry {
            String key;
            String value;
            Entry(String k, String v) { key = k; value = v; }
        }

        Entry[] table;
        int m;

        OpenHash(int size) {
            m = size;
            table = new Entry[m + 1];
        }

        int hash(String key) {
            return (key.hashCode() & 0x7fffffff) % m + 1;
        }

        void insert(String key, String value) {
            int i = hash(key);

            while (table[i] != null) {
                if (table[i].key.equals(key)) {
                    table[i].value = value;
                    return;
                }
                i++;
                if (i > m) i = 1;  
            }

            table[i] = new Entry(key, value);
        }

        String lookup(String key) {
            int i = hash(key);

            while (table[i] != null) {
                if (table[i].key.equals(key))
                    return table[i].value;

                i++;
                if (i > m) i = 1;  // wrap around
            }
            return null;
        }
    }

    /* ================= CHAINED HASH ================= */

    static class ChainedHash {

        static class Entry {
            String key;
            String value;
            Entry(String k, String v) { key = k; value = v; }
        }

        LinkedList<Entry>[] table;
        int m;

        @SuppressWarnings("unchecked")
        ChainedHash(int size) {
            m = size;
            table = new LinkedList[m + 1];
            for (int i = 1; i <= m; i++)
                table[i] = new LinkedList<>();
        }

        int hash(String key) {
            return (key.hashCode() & 0x7fffffff) % m + 1;
        }

        void insert(String key, String value) {
            int i = hash(key);

            for (Entry e : table[i]) {
                if (e.key.equals(key)) {
                    e.value = value;
                    return;
                }
            }
            table[i].add(new Entry(key, value));
        }

        String lookup(String key) {
            int i = hash(key);

            for (Entry e : table[i])
                if (e.key.equals(key))
                    return e.value;

            return null;
        }
    }

    /* ================= MAIN ================= */

    public static void main(String[] args) {

        DecimalFormat fiveD = new DecimalFormat("0.00000");

        // Generate keys
        String[] keys = new String[N];
        for (int i = 0; i < N; i++)
            keys[i] = Integer.toString(i + 1);

        // Shuffle
        List<String> list = Arrays.asList(keys);
        Collections.shuffle(list);
        list.toArray(keys);

        double[] alphas = {0.75, 0.80, 0.85, 0.90, 0.95};

        System.out.println("Load\tOpen(s)\t\tChained(s)");

        for (double alpha : alphas) {

            int n = USE;
            int m = (int)(n / alpha);   // table size

            double openTotal = 0;
            double chainTotal = 0;

            for (int r = 0; r < REPETITIONS; r++) {

                /* ----- OPEN HASH ----- */
                OpenHash open = new OpenHash(m);

                for (int i = 0; i < n; i++)
                    open.insert(keys[i], Integer.toString(i));

                long start = System.currentTimeMillis();

                for (int i = 0; i < n; i++)
                    open.lookup(keys[i]);

                long end = System.currentTimeMillis();
                openTotal += (end - start);

                /* ----- CHAINED HASH ----- */
                ChainedHash chain = new ChainedHash(m);

                for (int i = 0; i < n; i++)
                    chain.insert(keys[i], Integer.toString(i));

                start = System.currentTimeMillis();

                for (int i = 0; i < n; i++)
                    chain.lookup(keys[i]);

                end = System.currentTimeMillis();
                chainTotal += (end - start);
            }

            double openAvg = (openTotal / REPETITIONS) / 1000.0;
            double chainAvg = (chainTotal / REPETITIONS) / 1000.0;

            System.out.println(
                    alpha + "\t" +
                            fiveD.format(openAvg) + "\t" +
                            fiveD.format(chainAvg)
            );
        }
    }
}
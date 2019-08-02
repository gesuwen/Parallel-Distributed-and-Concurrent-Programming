package distributed;

import org.apache.spark.api.java.JavaPairRDD;
import scala.Tuple2;

import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

/**
 * A wrapper class for the implementation of a single iteration of the iterative
 * PageRank algorithm.
 */
public final class PageRank {
    /**
     * Default constructor.
     */
    private PageRank() {
    }
    /**
     * @param sites The connectivity of the website graph, keyed on unique
     *              website IDs.
     * @param ranks The current ranks of each website, keyed on unique website
     *              IDs.
     * @return The new ranks of the websites graph, using the PageRank
     *         algorithm to update site ranks.
     */
    public static JavaPairRDD<Integer, Double> sparkPageRank(
            final JavaPairRDD<Integer, Website> sites,
            final JavaPairRDD<Integer, Double> ranks) {
        JavaPairRDD<Integer, Double> newRanks = sites.join(ranks).flatMapToPair(kv -> {
            Integer websiteId = kv._1;
            Tuple2<Website, Double> value = kv._2;

            Website site = value._1;
            Double currentRank = value._2;
            Double rankRatioPart = currentRank / (double) site.getNEdges();

            List<Tuple2<Integer, Double>> contribs = new LinkedList<Tuple2<Integer, Double>>();
            Iterator<Integer> iter = site.edgeIterator();

            while (iter.hasNext()) {
                final int target = iter.next();
                contribs.add(new Tuple2<Integer, Double>(target, rankRatioPart));
            }
            return contribs;
        });
        return newRanks.reduceByKey((Double r1, Double r2) -> r1 + r2).mapValues(v -> v * 0.85 + 0.15);
    }
}

package org.mule.extension.opentelemetry.internal.exporter.metric.dynatrace;

import com.dynatrace.metric.util.*;
import com.google.common.annotations.VisibleForTesting;
import io.opentelemetry.api.common.AttributeType;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.metrics.data.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

final class Serializer {
    private static final Logger logger = Logger.getLogger(Serializer.class.getName());

    // the precision used to identify whether a percentile is the 0% (min) or 100% (max) percentile.
    private static final double PERCENTILE_PRECISION = 0.0001;

    private static final String TEMPLATE_ERR_METRIC_LINE =
            "Could not create metric line for data point with name %s (%s).";

    private static final String TEMPLATE_MSG_UNSUPPORTED_ATTRIBUTE_TYPE =
            "Skipping unsupported dimension with value type '%s'";

    private final MetricBuilderFactory builderFactory;

    Serializer(MetricBuilderFactory builderFactory) {
        this.builderFactory = builderFactory;
    }

    private Metric.Builder createMetricBuilder(MetricData metric, PointData point) {
        Metric.Builder builder =
                builderFactory
                        .newMetricBuilder(metric.getName())
                        .setDimensions(fromAttributes(point.getAttributes()));

        long epochNanos = point.getEpochNanos();
        // Only set a timestamp if it is available for the PointData.
        // If it is missing, the server will use the current time at ingest.
        if (epochNanos > 0) {
            builder.setTimestamp(Instant.ofEpochMilli(TimeUnit.NANOSECONDS.toMillis(epochNanos)));
        }
        return builder;
    }

    static List<Dimension> toListOfDimensions(Attributes attributes) {
        ArrayList<Dimension> dimensions = new ArrayList<>(attributes.size());
        attributes.forEach((k, v) -> {dimensions.add(Dimension.create(k.getKey(), String.valueOf(v)));});
        return dimensions;
    }

    static DimensionList fromAttributes(Attributes attributes) {
        return DimensionList.fromCollection(toListOfDimensions(attributes));
    }

    List<String> createLongSumLines(MetricData metric) {
        SumData<LongPointData> data = metric.getLongSumData();
        Collection<LongPointData> points = data.getPoints();
        List<String> lines = new ArrayList<>(points.size());
        boolean isMonotonic = data.isMonotonic();
        if (isMonotonic) {
            createLinesFromMonotonicLongSum(metric, lines, points);
        } else {
            createLinesFromNonMonotonicLongSum(metric, lines, points);
        }
        return lines;
    }

    private void createLinesFromMonotonicLongSum(
            MetricData metric, List<String> lines, Collection<LongPointData> points) {
        for (LongPointData point : points) {
            try {
                lines.add(
                        createMetricBuilder(metric, point)
                                // We always expect monotonic sums as deltas, which will be exported as delta
                                .setLongCounterValueDelta(point.getValue())
                                .serialize());
            } catch (MetricException me) {
                logger.warning(
                        () -> String.format(TEMPLATE_ERR_METRIC_LINE, metric.getName(), me.getMessage()));
            }
        }
    }

    private void createLinesFromNonMonotonicLongSum(
            MetricData metric, List<String> lines, Collection<LongPointData> points) {
        for (LongPointData point : points) {
            try {
                lines.add(
                        createMetricBuilder(metric, point)
                                // non-monotonic sums are exported as gauge.
                                .setLongGaugeValue(point.getValue())
                                .serialize());
            } catch (MetricException e) {
                logger.warning(
                        () -> String.format(TEMPLATE_ERR_METRIC_LINE, metric.getName(), e.getMessage()));
            }
        }
    }

    List<String> createLongGaugeLines(MetricData metric) {
        Collection<LongPointData> points = metric.getLongGaugeData().getPoints();
        List<String> lines = new ArrayList<>(points.size());
        for (LongPointData point : points) {
            try {
                lines.add(
                        createMetricBuilder(metric, point).setLongGaugeValue(point.getValue()).serialize());
            } catch (MetricException me) {
                logger.warning(
                        () -> String.format(TEMPLATE_ERR_METRIC_LINE, metric.getName(), me.getMessage()));
            }
        }
        return lines;
    }

    List<String> createDoubleGaugeLines(MetricData metric) {
        Collection<DoublePointData> points = metric.getDoubleGaugeData().getPoints();
        List<String> lines = new ArrayList<>(points.size());
        for (DoublePointData point : points) {
            try {
                lines.add(
                        createMetricBuilder(metric, point).setDoubleGaugeValue(point.getValue()).serialize());
            } catch (MetricException me) {
                logger.warning(
                        () -> String.format(TEMPLATE_ERR_METRIC_LINE, metric.getName(), me.getMessage()));
            }
        }
        return lines;
    }

    List<String> createDoubleSumLines(MetricData metric) {
        SumData<DoublePointData> data = metric.getDoubleSumData();
        Collection<DoublePointData> points = data.getPoints();
        List<String> lines = new ArrayList<>(points.size());
        boolean isMonotonic = data.isMonotonic();
        if (isMonotonic) {
            createLinesFromMonotonicDoubleSum(metric, lines, points);
        } else {
            createLinesFromNonMonotonicDoubleSum(metric, lines, points);
        }
        return lines;
    }

    private void createLinesFromMonotonicDoubleSum(
            MetricData metric, List<String> lines, Collection<DoublePointData> points) {
        for (DoublePointData point : points) {
            try {
                lines.add(
                        createMetricBuilder(metric, point)
                                // We always expect monotonic sums as deltas, which will be exported as they are
                                .setDoubleCounterValueDelta(point.getValue())
                                .serialize());
            } catch (MetricException me) {
                logger.warning(
                        () -> String.format(TEMPLATE_ERR_METRIC_LINE, metric.getName(), me.getMessage()));
            }
        }
    }

    private void createLinesFromNonMonotonicDoubleSum(
            MetricData metric, List<String> lines, Collection<DoublePointData> points) {
        // We always expect UpDownCounters to be exported as cumulative values, which will be serialized
        // as gauge.
        for (DoublePointData point : points) {
            try {
                lines.add(
                        createMetricBuilder(metric, point)
                                // non-monotonic sums are exported as gauge.
                                .setDoubleGaugeValue(point.getValue())
                                .serialize());
            } catch (MetricException e) {
                logger.warning(
                        () -> String.format(TEMPLATE_ERR_METRIC_LINE, metric.getName(), e.getMessage()));
            }
        }
    }

    List<String> createDoubleSummaryLines(MetricData metric) {
        Collection<SummaryPointData> points = metric.getSummaryData().getPoints();
        List<String> lines = new ArrayList<>(points.size());
        for (SummaryPointData point : points) {
            double min = Double.NaN;
            double max = Double.NaN;
            double sum = point.getSum();
            long count = point.getCount();

            for (ValueAtQuantile valueAtQuantile : point.getValues()) {
                if (Math.abs(valueAtQuantile.getQuantile() - 0.0) < PERCENTILE_PRECISION) {
                    // 0% quantile == minimum
                    min = valueAtQuantile.getValue();
                } else if (Math.abs(valueAtQuantile.getQuantile() - 100.0) < PERCENTILE_PRECISION) {
                    // 100% quantile == maximum
                    max = valueAtQuantile.getValue();
                }
            }

            if (Double.isNaN(min) || Double.isNaN(max)) {
                logger.warning(
                        () ->
                                "The min and/or max value could not be retrieved. This happens if the 0% and 100% quantile are not set for the summary. Using mean instead.");
                double mean = sum / count;
                min = mean;
                max = mean;
            }

            try {
                lines.add(
                        createMetricBuilder(metric, point)
                                .setDoubleSummaryValue(min, max, sum, count)
                                .serialize());
            } catch (MetricException me) {
                logger.warning(
                        () -> String.format(TEMPLATE_ERR_METRIC_LINE, metric.getName(), me.getMessage()));
            }
        }
        return lines;
    }

    List<String> createDoubleHistogramLines(MetricData metric) {
        // We always expect histograms as deltas.
        Collection<HistogramPointData> points = metric.getHistogramData().getPoints();
        List<String> lines = new ArrayList<>(points.size());
        for (HistogramPointData point : points) {
            double min = point.hasMin() ? point.getMin() : getMinFromBoundaries(point);
            double max = point.hasMax() ? point.getMax() : getMaxFromBoundaries(point);
            double sum = point.getSum();
            long count = point.getCount();

            try {
                lines.add(
                        createMetricBuilder(metric, point)
                                .setDoubleSummaryValue(min, max, sum, count)
                                .serialize());
            } catch (MetricException me) {
                logger.warning(
                        () -> String.format(TEMPLATE_ERR_METRIC_LINE, metric.getName(), me.getMessage()));
            }
        }
        return lines;
    }

    @VisibleForTesting
    static double getMinFromBoundaries(HistogramPointData pointData) {
        if (pointData.getCounts().size() == 1) {
            // In this case, only one bucket exists: (-Inf, Inf). If there were any boundaries, there
            // would be more counts.
            if (pointData.getCounts().get(0) > 0) {
                // in case the single bucket contains something, use the mean as min.
                return pointData.getSum() / pointData.getCount();
            }
            // otherwise, the histogram has no data. Use the sum as the min and max, respectively.
            return pointData.getSum();
        }

        // iterate all buckets to find the first bucket with count > 0
        for (int i = 0; i < pointData.getCounts().size(); i++) {
            if (pointData.getCounts().get(i) > 0) {
                // the current bucket contains something.
                if (i == 0) {
                    // In the first bucket, (-Inf, firstBound], use firstBound (this is the lowest specified
                    // bound overall). This is not quite correct but the best approximation we can get at this
                    // point. However, this might lead to a min bigger than the mean, thus choose the minimum
                    // of the following:
                    // - The lowest boundary
                    // - The average of the histogram (histogram sum / sum of counts)
                    return Math.min(
                            pointData.getBoundaries().get(i), pointData.getSum() / pointData.getCount());
                }
                // In all other buckets (lowerBound, upperBound] use the lowerBound to estimate min.
                return pointData.getBoundaries().get(i - 1);
            }
        }

        // there are no counts > 0, so calculating a mean would result in a division by 0. By returning
        // the sum, we can let the backend decide what to do with the value (with a count of 0)
        return pointData.getSum();
    }

    @VisibleForTesting
    static double getMaxFromBoundaries(HistogramPointData pointData) {
        // see getMinFromBoundaries for a very similar method that is annotated.
        if (pointData.getCounts().size() == 1) {
            if (pointData.getCounts().get(0) > 0) {
                return pointData.getSum() / pointData.getCount();
            }
            return pointData.getSum();
        }

        int lastElemIdx = pointData.getCounts().size() - 1;
        // loop over counts in reverse
        for (int i = lastElemIdx; i >= 0; i--) {
            if (pointData.getCounts().get(i) > 0) {
                if (i == lastElemIdx) {
                    // use the last bound in the bounds array. This can only be the case if there is a count >
                    // 0 in the last bucket (lastBound, Inf). In some cases, the mean of the histogram is
                    // larger than this bound, thus use the maximum of the estimated bound and the mean.
                    return Math.max(
                            pointData.getBoundaries().get(i - 1), pointData.getSum() / pointData.getCount());
                }
                // In any other bucket (lowerBound, upperBound], use the upperBound.
                return pointData.getBoundaries().get(i);
            }
        }

        return pointData.getSum();
    }
}
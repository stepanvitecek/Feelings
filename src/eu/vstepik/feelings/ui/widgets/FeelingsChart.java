package eu.vstepik.feelings.ui.widgets;

import java.util.List;
import java.util.Map;

import org.achartengine.chart.PointStyle;
import org.achartengine.chart.TimeChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;

public class FeelingsChart extends TimeChart {

	private static final long serialVersionUID = -8481586781698188056L;
	@SuppressWarnings("unused")
	private static final String TAG = "FeelingsChart";
	private static final int FEELINGS_COUNT = 7;

	public FeelingsChart(XYMultipleSeriesDataset dataset) {
		super(dataset, createRenderer(dataset));
		setDateFormat("d/M");
	}

	/**
	 * Disables legend space
	 */
	@Override
	protected int getLegendSize(DefaultRenderer renderer, int defaultHeight,
			float extraHeight) {
		return 0;
	}

	/**
	 * Moves X label up
	 */
	@Override
	protected void drawXLabels(List<Double> arg0, Double[] arg1, Canvas canvas,
			Paint arg3, int arg4, int arg5, int arg6, double arg7, double arg8,
			double arg9) {
		super.drawXLabels(arg0, arg1, canvas, arg3, arg4, arg5,
				canvas.getHeight() - 50, arg7, arg8, arg9);
	}

	/**
	 * Disables Y label text, but keeps lines
	 */
	@Override
	protected void drawYLabels(Map<Integer, List<Double>> allYLabels,
			Canvas canvas, Paint paint, int maxScaleNumber, int left,
			int right, int bottom, double[] yPixelsPerUnit, double[] minY) {
		Orientation or = mRenderer.getOrientation();
		boolean showGridX = mRenderer.isShowGridX();
		boolean showLabels = mRenderer.isShowLabels();
		for (int i = 0; i < maxScaleNumber; i++) {
			paint.setTextAlign(mRenderer.getYLabelsAlign(i));
			List<Double> yLabels = allYLabels.get(i);
			int length = yLabels.size();
			for (int j = 0; j < length; j++) {
				double label = yLabels.get(j);
				Align axisAlign = mRenderer.getYAxisAlign(i);
				boolean textLabel = mRenderer.getYTextLabel(label, i) != null;
				float yLabel = (float) (bottom - yPixelsPerUnit[i]
						* (label - minY[i]));
				if (or == Orientation.HORIZONTAL) {
					if (showLabels && !textLabel) {
						paint.setColor(mRenderer.getYLabelsColor(i));
						if (axisAlign == Align.LEFT) {
							canvas.drawLine(left + getLabelLinePos(axisAlign),
									yLabel, left, yLabel, paint);
						} else {
							canvas.drawLine(right, yLabel, right
									+ getLabelLinePos(axisAlign), yLabel, paint);
						}
					}
					if (showGridX) {
						paint.setColor(mRenderer.getGridColor());
						canvas.drawLine(left, yLabel, right, yLabel, paint);
					}
				} else if (or == Orientation.VERTICAL) {
					if (showLabels && !textLabel) {
						paint.setColor(mRenderer.getYLabelsColor(i));
						canvas.drawLine(right - getLabelLinePos(axisAlign),
								yLabel, right, yLabel, paint);
					}
					if (showGridX) {
						paint.setColor(mRenderer.getGridColor());
						canvas.drawLine(right, yLabel, left, yLabel, paint);
					}
				}
			}
		}
	}

	/**
	 * Set renderer option for feelings graph
	 * 
	 * @param dataset
	 * @return
	 */
	private static XYMultipleSeriesRenderer createRenderer(
			XYMultipleSeriesDataset dataset) {
		XYSeries series = dataset.getSeriesAt(0);
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setLabelsTextSize(25);
		renderer.setPointSize(10);
		renderer.setMargins(new int[] { 0, 0, 0, 0 });
		renderer.setYAxisMin(-0.5);
		renderer.setYAxisMax(FEELINGS_COUNT - 0.5);
		renderer.setYLabels(FEELINGS_COUNT);
		// display days to the future
		if (series.getItemCount() == 1) {
			renderer.setXAxisMax(series.getMaxX() + TimeChart.DAY * 2);
		} else {
			renderer.setXAxisMax(series.getMaxX() + TimeChart.DAY);
		}
		// display days to the past
		if (series.getItemCount() == 1) {
			renderer.setXAxisMin(series.getMinX() - TimeChart.DAY * 2);
		} else if (series.getItemCount() < 7) {
			renderer.setXAxisMin(series.getMinX() - TimeChart.DAY);
		} else {
			renderer.setXAxisMin(series.getX(series.getItemCount() - 7));
		}
		renderer.setAntialiasing(true);
		renderer.setShowLegend(false);
		renderer.setShowGrid(true);
		renderer.setShowAxes(false);
		renderer.setPanEnabled(true, false);
		renderer.setZoomEnabled(true, false);
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setPointStyle(PointStyle.CIRCLE);
		r.setLineWidth(7);
		r.setColor(Color.parseColor("#000000"));
		r.setFillPoints(true);
		renderer.addSeriesRenderer(r);
		return renderer;
	}

	/**
	 * Protected method I had to create as well
	 * 
	 * @param align
	 * @return
	 */
	private int getLabelLinePos(Align align) {
		int pos = 4;
		if (align == Align.LEFT) {
			pos = -pos;
		}
		return pos;
	}

}

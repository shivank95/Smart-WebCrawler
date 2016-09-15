import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.DefaultIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.ui.ApplicationFrame;
 
@SuppressWarnings("serial")
public class PieChart_AWT extends ApplicationFrame 
{
	String[] words = new String[100];
	int[] values = new int[100];
	
   public PieChart_AWT( String title) 
   {
      super( title ); 
      setContentPane(createDemoPanel( ));
   }
   
   public PieChart_AWT( String title, String[] words, int[] values) {
	   
	   super(title);
	   
	   this.words = new String[100];
	   this.values = new int[100];
	   
	   this.words = words;
	   this.values = values;
	   

	   setContentPane(createDemoPanel( ));
   }
   
   private HistogramDataset createDataset() 
   {
	   HistogramDataset dataset = new HistogramDataset( );
	   
	   double[] values2 = new double[values.length];
	   for (int i =0; i < values.length; i++) {
		   values2[i] = (double)values[i];
	   }
	   
	  for (int i = 0; i < 100; i++) {
		  
		  dataset.addSeries(words[i], values2, 5);
	  }
	    
      return dataset;         
   }
   private JFreeChart createChart( HistogramDataset dataset )
   {
      JFreeChart chart = ChartFactory.createHistogram(      
         this.getTitle(),  // chart title 
         "Words",	//X axis
         "Frequency", //Y axis
         dataset,	//Data
         PlotOrientation.VERTICAL,
         false,           // include legend   
         false, 
         false);

      return chart;
   }
   public JPanel createDemoPanel( )
   {
      JFreeChart chart = createChart(createDataset( ) );  
      return new ChartPanel( chart ); 
   }
}

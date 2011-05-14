package hudson.plugins;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.views.ListViewColumn;

import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

/**
 * UpstreamDownstreamViewColumn
 * 
 * 
 * 12/05/2011
 * 
 * @author Kenji Kawaji
 */
public class UpDownStreamViewColumn extends ListViewColumn {

	public static final int UPSTREAM = 1;
	public static final int DOWNSTREAM = 2;
	public static final String NOT_AVAILABLE = "N/A";
	private static final int MAX_COLUMN_WIDTH = 50;

	/**
	 * @return HTML String containing the cron expression of each Trigger on the
	 *         Job (when available).
	 */
	public String getStreamInfo(Job job, int streamType) {
		if (!(job instanceof AbstractProject<?, ?>))
			return "";

		StringBuilder expression = new StringBuilder();

		AbstractProject<?, ?> project = (AbstractProject<?, ?>) job;
		if (streamType == UPSTREAM) {
			expression
					.append(getHTMLProjectInfo(project.getUpstreamProjects()));
		} else if (streamType == DOWNSTREAM) {
			expression.append(getHTMLProjectInfo(project
					.getDownstreamProjects()));
		}
		return expression.toString();
	}

	private String getHTMLProjectInfo(List <AbstractProject> lst) {
    	StringBuilder processingString = new StringBuilder();
    	StringBuilder expression = new StringBuilder();
    	boolean firstElement = true;
    	Iterator <AbstractProject> itr = lst.iterator();
    	while(itr.hasNext()){
    		AbstractProject prj = itr.next();
    		if (!firstElement){
    			processingString.append("&nbsp");
    		}    		
    		String linkString = new String("<a href=\"" + prj.getUrl() + "\">" + prj.getName() + "</a>");    		
    		if ((processingString.length() + prj.getName().length() + 1) < MAX_COLUMN_WIDTH) {
    			processingString.append(linkString);
    			
    		} else if (processingString.length() < 1 && ((prj.getName().length()) > MAX_COLUMN_WIDTH)) {
    			processingString.append(linkString + "<br/>");
    			expression.append(processingString );
    			processingString = new StringBuilder();
    		} else {
    			processingString.append("<br/>");
    			expression.append(processingString);
    			processingString = new StringBuilder();
    			processingString.append(linkString);
    		}
    		firstElement = false;
    	}
    	expression.trimToSize();
    	if (expression.length() < 1 && processingString.length() < 1) {
    		expression.append(NOT_AVAILABLE);
    	} else if (processingString.length() > 0) {
    		expression.append(processingString);
    	}
    	return expression.toString();
    }	
	
	@Extension
	public static final Descriptor<ListViewColumn> DESCRIPTOR = new Descriptor<ListViewColumn>() {
		@Override
		public ListViewColumn newInstance(StaplerRequest req,
				JSONObject formData) {
			return new UpDownStreamViewColumn();
		}

		@Override
		public String getDisplayName() {
			return "Upstream Downstream View Plugin";
		}
	};

	public Descriptor<ListViewColumn> getDescriptor() {
		return DESCRIPTOR;
	}
}
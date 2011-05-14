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
 * UpstreamDownstreamViewColumn, this plugin allows used to see the two additional
 * colums containing the Upstreamed and Downstreamed jobs for the particular job. 
 * Additionally this plugin in the current version cut the column length to 50 chars
 *  in case if the job name summary length is less than the 50 or it put one job name
 *  in the column in case if the job name is fewer than 50 chars.
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
	 * This method will returns the HTML representation of the 
	 * Upstreamed/Downstreamed jobs for the particular master job.
	 *
	 * @return HTML String containing the Upstreamed/ Downstreamed jobs undert the
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
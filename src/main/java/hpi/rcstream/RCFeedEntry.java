package hpi.rcstream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by magnus on 19.04.16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class RCFeedEntry {

    public class Revision {
        public String _old;
        public String _new;
    }

    public class Length {
        public String _old;
        public String _new;
    }

    public long id; // 215733984
    public String type; // "categorize"
    public int namespace; // 14
    public String title; // "Category:Male Dutch pronunciation"
    public String comment; // "[[:File:Nl-geheugenplaatsjes.ogg]] added to category"
    public long timestamp; // 1461069130
    public String user; // "RileyBot"
    public Boolean bot; // true
    public String server_url; // "https://commons.wikimedia.org"
    public String server_name; // "commons.wikimedia.org"
    public String server_script_path; // "/w"
    public String wiki; // "commonswiki"
    public Boolean minor = false;
    public Boolean patrolled = false;
    public Object length;
    public Object revision;
    public int log_id;
    public String log_type;
    public String log_action;
    public Object log_params;
    public String log_action_comment;

    @Override
    public String toString() {
        return type.toUpperCase() + " " + id + ": " + title + " " + user + (bot ? "(bot) " : " ");
    }
}

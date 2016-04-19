package hpi.rcstream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by magnus on 19.04.16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RCFeedEntry {

    public String comment; // "[[:File:Nl-geheugenplaatsjes.ogg]] added to category"
    public String wiki; // "commonswiki"
    public String server_name; // "commons.wikimedia.org"
    public String title; // "Category:Male Dutch pronunciation"
    public long timestamp; // 1461069130
    public String server_script_path; // "/w"
    public int namespace; // 14
    public String server_url; // "https://commons.wikimedia.org"
    public String user; // "RileyBot"
    public Boolean bot; // true
    public String type; // "categorize"
    public long id; // 215733984

    public Object length;

    @Override
    public String toString() {
        return type.toUpperCase() + " " + id + ": " + title + " " + user + (bot?"(bot) ":" ");
    }
}

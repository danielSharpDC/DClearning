package apps.dcc.com.dclearning;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class Cours  {
    private final long id;
    private final String name;
    private final String dep;

    public Cours(JSONObject jObject) {
        this.id = jObject.optLong("id");
        this.name = jObject.optString("c_nom");
        this.dep = jObject.optString("c_dep");
    }

    public long getId() { return id; }

    public String getName() { return name; }

    public String getDep() { return dep; }
}

package eu.vstepik.feelings.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Data object for one feeling
 *
 * @author vstepik, destil
 */
public class Feeling implements Serializable {
    private static final long serialVersionUID = 2795826801404948486L;
    public int value; // value 0-8
    public long created;
    public String note;

    public Feeling(int feeling, long datetime, String note) {
        this.value = feeling;
        this.created = datetime;
        this.note = note;
    }

    public static final class Feelings implements BaseColumns {

        @SuppressWarnings("unused")
        private static final String TAG = "Payments";

        private Feelings() {
        }

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + Database.AUTHORITY + "/feelings");

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.destil.feeling";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.destil.feeling";

        public static final String _ID = "_id";

        public static final String VALUE = "feeling";

        public static final String CREATED = "created";

        public static final String NOTE = "note";

        /**
         * Returns all feelings in the database.
         */
        public static List<Feeling> getAll(Context c) {
            Cursor cursor = c.getContentResolver().query(
                    Feelings.CONTENT_URI,
                    new String[] { Feelings.VALUE, Feelings.CREATED,
                            Feelings.NOTE }, null, null, null);
            List<Feeling> feelings = new ArrayList<Feeling>();
            while (cursor.moveToNext()) {
                Feeling feeling = new Feeling(cursor.getInt(0),
                        cursor.getLong(1), cursor.getString(2));
                feelings.add(feeling);
            }
            cursor.close();
            return feelings;
        }
    }
}
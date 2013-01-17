package de.hwrberlin.app.prostapp.g2;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.IOException;

/**
 * Klasse zum Löschen eines Events im Kalender
 * 
 * @author M.Funk and P. Köhn
 *
 */
class AsyncDeleteEvent extends AsyncTask<Void, Void, Void> {
  private final CalendarSample calendarSample;
  private final ProgressDialog dialog;
  private final int calendarIndex;
  private com.google.api.services.calendar.Calendar client;
  private final String sEventId;

  AsyncDeleteEvent(CalendarSample calendarSample, int calendarIndex, String sEventId) {
    this.calendarSample = calendarSample;
    client = calendarSample.client;  
    this.calendarIndex = calendarIndex;
    this.sEventId = sEventId;
    dialog = new ProgressDialog(calendarSample);
  }

  @Override
  protected void onPreExecute() {
    dialog.setMessage("Event wird gelöscht...");
    dialog.show();
  }

  @Override
  protected Void doInBackground(Void... arg0) {
    String calendarId = calendarSample.calendars.get(calendarIndex).id;
    try{
        //Event über die API löschen
        client.events().delete(calendarId, sEventId).execute();
        
        //Event aus DB entfernen 
        UpdateEvents updateEvents = new UpdateEvents();
        updateEvents.deleteEvent(sEventId);
        
    } catch (IOException e) {
      calendarSample.handleGoogleException(e);
    } finally {
      calendarSample.onRequestCompleted();
    }
    return null;
  }

  @Override
  protected void onPostExecute(Void result) {
    dialog.dismiss();
    calendarSample.refresh();
  }
}

package de.hwrberlin.app.prostapp.g2;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author M. Funk and P. K�hn
 *
 */
class AsyncAddEvent2 extends AsyncTask<Void, Void, Void> {
  private final CalendarSample calendarSample;
  private final ProgressDialog dialog;
  private final int calendarIndex;
  private final String sVlName;
  private final String sFrequency;
  private final String sStartDate;
  private final String sEndDate;  
  private com.google.api.services.calendar.Calendar client;
  private static final String TAG = "AsyncAddEvent2-Klasse";

  AsyncAddEvent2(CalendarSample calendarSample, int calendarIndex, String sVlName, String sStartDate, String sEndDate, String sFrequency) {
    this.calendarSample = calendarSample;
    client = calendarSample.client;
    this.calendarIndex = calendarIndex;
    dialog = new ProgressDialog(calendarSample);
    this.sVlName = sVlName;
    this.sFrequency = sFrequency;
    this.sStartDate = sStartDate;
    this.sEndDate = sEndDate; 
  }

  @Override
  protected void onPreExecute() {
    dialog.setMessage("Adding event...");
//    dialog.show();
  }

  @Override
  protected Void doInBackground(Void... arg0) {
    String calendarId = calendarSample.calendars.get(calendarIndex).id;
    Date dateStart = null;
    Date dateEnd= null; 
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");//Format f�r RFC3339  
    
    //Datum-Formatierung: Date -> DateTime -> EventTime
    try 
    {
      dateStart = sdf.parse(sStartDate);
      dateEnd = sdf.parse(sEndDate);
      
    } catch (ParseException exception) 
    {
      exception.printStackTrace();
      Log.d(TAG, "Fehler beim parsen eines Datums");
    }    
    DateTime dtStart = new DateTime(dateStart, TimeZone.getTimeZone("UTC"));
    DateTime dtEnd = new DateTime(dateEnd, TimeZone.getTimeZone("UTC"));
    
    Event event = new Event();    
    event.setStart( new EventDateTime().setDateTime(dtStart).setTimeZone("UTC"));
    event.setEnd( new EventDateTime().setDateTime(dtEnd).setTimeZone("UTC"));
    event.setSummary(sVlName);
    event.setLocation("HWR Sch�neberg, Berlin");
    event.setColorId("11"); //11 => rot
    event.setDescription("Hier k�nnte eine sinnvolle Beschreibung stehen");
    
    if( sFrequency != null  )
    {
        if( sFrequency.equals("WEEKLY") || sFrequency.equals("DAILY")  )
        {
          String sRecurrenceContent = "RRULE:FREQ=" + sFrequency;
//          String sRecurrenceContent = "RRULE:FREQ=" + sFrequency + ";UNTIL=20130303T100000+00:00";
          Log.d(TAG, "sRecContent: " + sRecurrenceContent);
          event.setRecurrence(Arrays.asList(sRecurrenceContent));
        }
     }     
     try {
      //Event eintragen
      Event createdEvent = client.events().insert(calendarId, event).execute();
      Log.d(TAG, "Event-ID: " + createdEvent.getId());
      
      //Google-Event-ID zum speichern in der DB �bergeben. 
      UpdateEvents updateEvents = new UpdateEvents();
      updateEvents.addEventId(createdEvent.getId(), sVlName);
      
    } catch (IOException e) {
      calendarSample.handleGoogleException(e);
    } finally {
      calendarSample.onRequestCompleted();
    }
    return null;
  }

  @Override
  protected void onPostExecute(Void result) {
//    dialog.dismiss();
    calendarSample.refresh();
  }
  
  
  @SuppressWarnings("unused")
  private void listEvents(String calendarId)
  {
  //Liste aller events eines Kalenders ausgeben
    Events eventsList = null;
    try {
      eventsList = client.events().list(calendarId).execute();
    } catch (IOException exception) {
      // TODO Auto-generated catch block
      exception.printStackTrace();
    }
    while (true) 
    {
      Log.d(TAG, "Eventliste:");
      for (Event eItem : eventsList.getItems()) 
      {
        Log.d(TAG, "Event-Name: " + eItem.getSummary());
      }
      String pageToken = eventsList.getNextPageToken();
      if (pageToken != null && !pageToken.isEmpty()) 
      { //Achtung, 'isEmpty() ben�tigt min API9.  Manifest wurde dementsprechend angepasst.
        try {
          eventsList = client.events().list("primary").setPageToken(pageToken).execute();
        } catch (IOException exception) {
          // TODO Auto-generated catch block
          exception.printStackTrace();
        }
      } else 
      {
        break;
      }
    }
  }
}

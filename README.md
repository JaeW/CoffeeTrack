# CoffeeTrack
Collects data on where, when and how much coffee is consumed by user


After realizing that (more than once) I had consumed an entire pot of coffee within far too short a timeframe, I knew I needed an app.
Drink a cup of coffee and tap a button on my smartphone.  Have a widget that shows me the last time I drank a cup so that I don't end up 
<a href="https://www.youtube.com/watch?v=4NnkhMGXrp8&ab_channel=ExtremeGimp">like this</a>.

To be implemented in 3 phases:

1.  Simple tracking of each time coffee is consume.  Data recorded in a SQLite database through a Content Provider. Listing of each 
time coffee was consumed in main activity with FAB button serving to save a new cup of coffee record.  Additionally, a home screen
widget will display the last time a cup was consumed along with a button to record another cup of coffee was consumed.  Overflow menu
provides option to view coffee consumption for the past day, week and month.  

2.  Addition of a session concept - where a session (study, work, whatever) can be started when the first cup of coffee is consumed.
thereafter the user will be reminded at regular, periodic intervals to have another cup.  This keeps the user from drinking too much 
(and turning into a stress monkey) or too little (falling asleep on the keyboard).  Implementation will involve notifications;
optionally notifications utilizing the direct reply option accessible on the lock screen.  

3.  When notifications from phase 2 implementation appear, user is offered option of finding coffee shops closest to his/her current
location.  

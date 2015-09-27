# Vsynchistory
vsynchistory is a program, to track changes of vcalendars and vcard data.
Furthermore it's provides a functionality to archive entries.

It's work's fine together with Radicale.

## Usage
Create a `application.properties` with following entry
`repositoryLocation=/home/radicale/collections`.
Start the programm with
`java -jar vsynchistory.jar`

## How it works
The archive function takes all calendar entries which enddate and
last-modified-date is before the 1.7.<current-year - 1>.

## License
The programm is licensed under the AGPL.

## TODO
- the archive-calendars are hard-coded.
- at the moment there is no vcard support.
- better documentation.

# Notetaker

### **Description of Problem Domain:**
The problem domains our team is looking to explore are the Time Management and Notetaking domains.

### **Higher Level Description of Application Type:**
Essentially, our team is looking to develop a software version of the productivity system referred to in the
book, *Getting Things Done by David Allen*. This system involves creating an initial list of items/thoughts that need to be addressed. You then clarify
each item into **Actionable** or **Not Actionable**.

**Actionable** Items are sorted into three categories:
- If it can be done in under 2 minutes, it is put into the Next Actions Section:
- If the actionable item can/should be split up into smaller tasks over time, it is put into the Projects
Section
- If it is a time sensitive item such as an appointment or event, it is scheduled into your calendar.

**Non-Actionable** Items are sorted into two categories:
- An idea that cannot be prioritized immediately is put into Someday/Maybe section.
- If it is something that will need referencing for later, it is put into the References section.

# API

We hope to use the [Google Calendar API](https://developers.google.com/calendar/api/guides/overview) to synchronize actionable events with the user’s
calendar.

Some other helpful documentation
- https://developers.google.com/calendar/api/v3/reference/events/insert
- https://developers.google.com/identity/protocols/oauth2/native-app
# Author: Byron
#
# How to use:
# sudo apt-get install python-mysqldb
# python populate_course_data.py

import csv
import MySQLdb

# Connect to DB
db = MySQLdb.connect(host='localhost', user='pod4', passwd='groupup', db='groupup')
cursor = db.cursor()

invalid_course_ids = []

try:
    # Read courses file and insert into DB
    reader = csv.reader(open('courses.txt', 'rb'), delimiter=',', quotechar='"')
    for row in reader:
        if row[4] == '1-2': # 2-semester courses are encoded as being in term 0
            row[4] = '0'
        if row[4] not in map(str, range(0,3)): # Not sure what to do with courses with term A,B,C,D. Ignore them?
            invalid_course_ids.append(row[0])
            continue
        query = "INSERT INTO `groupup_course` (id, coursenum, dept, section, term) " + \
                "VALUES (%s, %s, %s, %s, %s)"
        cursor.execute(query, tuple(row))
    db.commit()

    # Read timeslots file and insert into DB
    reader = csv.reader(open('timeslots.txt', 'rb'), delimiter=',', quotechar='"')
    i = 0
    for row in reader:
        i += 1
        if row[0] in invalid_course_ids:
            continue
        row.insert(0, i)
        row.append(1)
        # Assume recurrence values: 0 = one-time, 1 = weekly, can add more later. All course timeslots are weekly.
        query = "INSERT INTO `groupup_timeslot` (id, course_id, day_of_week, start_time, end_time, reccurance) " + \
                "VALUES (%s, %s, %s, %s, %s, %s)"
        cursor.execute(query, tuple(row))
    db.commit()
except Exception as e:
    print "Error: Database is not empty, make sure to truncate the tables `groupup_course` and `groupup_timeslot`"

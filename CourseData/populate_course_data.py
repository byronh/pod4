# Author: Byron
#
# How to use:
# sudo apt-get install python-mysqldb
# python populate_course_data.py

import csv
import datetime
import MySQLdb

# Connect to DB
db = MySQLdb.connect(host='localhost', user='pod4', passwd='groupup', db='groupup')
cursor = db.cursor()

invalid_course_ids = []

# Read courses file and insert into DB
reader = csv.reader(open('courses.txt', 'rb'), delimiter=',', quotechar='"')
for row in reader:
    if row[4] == '1-2': # 2-semester courses are encoded as being in term 0
        row[4] = '0'
    if row[4] not in map(str, range(0,3)): # Not sure what to do with courses with term A,B,C,D. Ignore them?
        invalid_course_ids.append(row[0])
        continue
    query = "INSERT INTO `groupup_course` (id, dept, coursenum, section, term) " + \
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

    try:
        start_hour, start_minute = map(int, row[2].split(':'))
    except ValueError:
        start_minute = 0

    start_offset = datetime.timedelta(hours=start_hour, minutes=start_minute)

    try:
        end_hour, end_minute = map(int, row[3].split(':'))
    except ValueError:
        end_minute = 0

    end_offset = datetime.timedelta(hours=end_hour, minutes=end_minute)

    row[2] = datetime.datetime.combine(datetime.date.today(), datetime.time()) + start_offset
    row[3] = datetime.datetime.combine(datetime.date.today(), datetime.time()) + end_offset

    row.insert(0, i)
    row.append(1)

    # Assume recurrence values: 0 = one-time, 1 = weekly, can add more later. All course timeslots are weekly.
    query = "INSERT INTO `groupup_timeslot` (id, course_id, day_of_week, start_time, end_time, reccurance) " + \
            "VALUES (%s, %s, %s, %s, %s, %s)"
    cursor.execute(query, tuple(row))
    cursor.execute('update SEQUENCE SET SEQ_COUNT=20000 where SEQ_NAME="SEQ_GEN"')
db.commit()

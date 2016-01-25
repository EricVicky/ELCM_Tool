#!/usr/bin/expect

export PATH=/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin:/alcatel/omc1/OMC_MON/scripts:/alcatel/omc1/OMC_MON/scripts/tools:/usr/lib/jvm/java/jre/bin:/usr/lib/jvm/java/bin

spawn /export/home/omcmon/usr/bin/replicate_data full

expect "Do you want to continue? Enter 'y' (anything else exits) ->"

send "y\r"

interact


#!/usr/bin/expect

set path = "$PATH":/alcatel/omc1/OMC_MON/scripts/tools

spawn /export/home/omcmon/usr/bin/replicate_data full

expect "Do you want to continue? Enter 'y' (anything else exits) ->"

send "y\r"

interact


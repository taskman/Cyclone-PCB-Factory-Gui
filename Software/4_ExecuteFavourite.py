#!/usr/bin/python

# AUTHOR:
#   Carlosgs (http://carlosgs.es)
# LICENSE:
#   Attribution - Share Alike - Creative Commons (http://creativecommons.org/licenses/by-sa/3.0/)
#
# DISCLAIMER:
#   This software is provided "as is", and you use the software at your own risk. Under no
#   circumstances shall Carlosgs be liable for direct, indirect, special, incidental, or
#   consequential damages resulting from the use, misuse, or inability to use this software,
#   even if Carlosgs has been advised of the possibility of such damages.

# Begin configuration
from configuration import * # load settings
# End configuration

# Begin modules
from misc import *
import ConfigParser

import CycloneHost.Controller as cy
# End modules


cy.connect(BAUDRATE, DEVICE, Emulate)
sys.stdout.flush()

cy.sendCommand("G90\n") # Set absolute positioning
sys.stdout.flush()

#read gcode file
gcodeFileName = sys.argv[1]
print("Executing " + gcodeFileName)
with open(favouritesPath + gcodeFileName + ".gcode", 'r') as gcodeFile:
    for gcode in gcodeFile:
        print("Sending command " + gcode.rstrip())
        sys.stdout.flush()
        cy.sendCommand(gcode + "\n")
    
gcodeFile.close()

print("Done")

cy.close() # Close the connection with Cyclone

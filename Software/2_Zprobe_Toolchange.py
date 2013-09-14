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

# Load the Z data file
Z_probing_data = loadFromFile(Z_PROBING_FILE)


cy.connect(BAUDRATE, DEVICE, Emulate)

cy.sendCommand("G90\n") # Set absolute positioning

cy.homeZXY() # Home all the axis


Z_origin_offset = cy.probeZ()
print("Z offset: " + str(Z_origin_offset) )
sys.stdout.flush()

cy.close() # Close the connection with Cyclone


config = ConfigParser.RawConfigParser()
config.add_section('Z_Probing')
config.set('Z_Probing', 'Z_origin_offset', Z_origin_offset)

with open('cyclone.cfg', 'wb') as configfile:
    config.write(configfile)
#!/bin/bash
javafxpackager -deploy -native -outdir build -outfile SQLibri-0.4.2 -srcdir build/libs/ -srcfiles sqlibri-0.4.2.jar -appclass com.sqlibri.App -name "SQLibri" -title "SQLibri"

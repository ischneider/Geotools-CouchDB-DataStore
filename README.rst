CouchDB DataStore
=================

Temporary home for work before pushing into subversion.

Installing Geotools Module
--------------------------

This can be pulled into a geotools checkout by::
  
  git submodule add git://github.com/ischneider/Geotools-CouchDB-DataStore.git modules/unsupported/couchdb/

Or replace the read-only URL with the SSH URL for write access.

For details on removing the submodule, see here (at the bottom): https://git.wiki.kernel.org/index.php/GitSubmoduleTutorial

.. attention:: To run tests, you need an accessible local (or remote) couch instance
   In addition, the couchdb.properties fixture needs to be configured to point
   to the instance.

Installing Couch/Geocouch
-------------------------

The easiest way to install couch is to obtain the installer from couchbase at:
http://www.couchbase.com/downloads/couchbase-server/community

Functionality
-------------

The CouchDBDataStore supports reading and writing (add only) with native couch 
spatial bbox queries. This should be considered a alpha version with minimal
tests and error handling.

The current 'design document' in use for the test cases is not necessarily the
final solution and is acknowledged to have shortcomings. This is one aspect for
future discussion and work - coming up with a set of supported couch designs
and branching internally on detection/recognition of a design.
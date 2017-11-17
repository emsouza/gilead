Gilead
======

* Gilead fork for GWT 2.6.1+, Hibernate 5.0 and WildFly 10.1.

Gilead permits you to use your Persistent POJO (and especially the partially loaded ones) outside the JVM (GWT) without pain. No lazy initialisation or serialization exception. Just POJO and Domain Driven Design.


Clone and merge operations
--------------------------

To work properly with Hibernate beans, GWT needs them to be real POJO, ie without any instrumented class and basic collection implementations. Removing proxies and replacing persistent collections is the job of the clone operation.
Symetrically, Hibernate needs associations to be set with proxies when property was not loaded, and persistence information on collections (for dirty state checking and so on...). Rebuild such instrumented and persistent beans is the goal of the merge operation.

Beanlib
--------------------------

[Beanlib for Hibernate](https://github.com/emsouza/beanlib)
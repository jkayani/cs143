# Revisions necessary to run grading script since test cases were created using 
# a very old version of SPIM. We replace the first 4 lines (copyright notice etc)
# leaving just the "Loaded exceptions.s" message and the program output

ls | grep cl.out | xargs -I % sed -e '1,4d' -i %

# Symlink expected spim to /usr/local/bin/spim ?

# Symlink expected Java classpath to /usr/class/cs143/lib ? 
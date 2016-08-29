# tt = Transaction Time
# adds <logic:true> in front and "0"^^<xsd:long> to the end of a tuple;
# assumes that the input file is in N-Triple/Tuple form and ends in '.';
# call with:  tt.sh <infile> <outfile>
cat $1 |
while read line
  do
    echo '<logic:true>' ${line/%./} '"0"^^<xsd:long> .'
  done > $2
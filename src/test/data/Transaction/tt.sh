getRandomValue(){
  shuf -i $1-10000 -n 1
}

cat $1 |
while read line
  do
    VAR=$(shuf -i 0-10000 -n 1)
    echo '"'$VAR'"<xsd:long>' ${line/%./} ' .'
  done > $2

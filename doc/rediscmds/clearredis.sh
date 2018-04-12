#!/bin/sh

todel=todel.keys.txt
> $todel
sh myredis --raw keys 'm:*' >> $todel
sh myredis --raw keys 'sync:*' >> $todel
sed -i -e '/^$/d' $todel
[ -s $todel ] || exit 0

sed -i -e 's/^/del /g' $todel
unix2dos $todel
[ -s $todel ] && sh myredis --pipe <$todel



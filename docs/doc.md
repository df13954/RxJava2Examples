## 忽略文件夹

1, 不小心把.idea中文件加入版本控制,需要移除.

# .gitignore file
.idea/

git rm -r --cached .idea


git commit -m "Ignore .idea directory"


即可忽略.
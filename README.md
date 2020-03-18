# PippyDetection
一个基于图分析的恶意检测工具

# 复现论文
Fast, Scalable Detection of “Piggybacked” Mobile Applications 作者：Wu Zhou, Yajin Zhou, Michael Grace, Xuxian Jiang, and Shihong Zou

# 功能
上传一个apk文件，可以分析得到其主模块和副模块

# 检测步骤
第一步：分析apk路径，将其反编译，存到文件中

第二步：将反编译文件按照目录结构建树，smali目录对应的一级目录为根，一般而言，一个apk对应多棵树

第三步：将树中的根包作为图节点建立图，深度搜索树找到所有的叶子节点即类

第四步：深度搜索所有树，对每个类文件新建一个线程，线程分析文件且建立一个当前类到所有根包所在的类的映射关系，

第五步，将所有映射关系对应到类所在根包直接的联系，然后根据根包联系的大小聚类得到多个模块，最后根据条件确定主副模块

# 使用方法
下载rar文件，修改Test.Main中的apk路径为你想要分析的apk路径，使用java编译器编译运行即可

# 其他事项
当前版本为测试版本，只能针对一个apk进行检测，多apk检测版本后续会更新

# 版权所有
ouou

#!/system/bin/sh

SKIPUNZIP=1

ui_print "****************************************"
ui_print "*        短信转发助手 (PushPlus)       *"
ui_print "*             版本 v1.0.0              *"
ui_print "****************************************"

ui_print "- 正在解压模块文件..."
unzip -o "$ZIPFILE" 'module.prop' -d "$MODPATH" >&2
unzip -o "$ZIPFILE" 'service.sh' -d "$MODPATH" >&2
unzip -o "$ZIPFILE" 'system/*' -d "$MODPATH" >&2

# 设置权限
ui_print "- 正在设置文件权限..."
set_perm_recursive "$MODPATH" 0 0 0755 0644
set_perm "$MODPATH/service.sh" 0 0 0755
set_perm_recursive "$MODPATH/system/priv-app/SmsForwarder" 0 0 0755 0644

# 尝试自动安装 APK (如果系统没有自动识别 priv-app)
APK_PATH="$MODPATH/system/priv-app/SmsForwarder/SmsForwarder.apk"
if [ -f "$APK_PATH" ]; then
  ui_print "- 正在尝试直接安装 APK..."
  pm install -r "$APK_PATH" >/dev/null 2>&1
  if [ $? -eq 0 ]; then
    ui_print "  安装成功！"
  else
    ui_print "  直接安装失败，重启后系统将自动安装。"
  fi
fi

ui_print "- 安装完成！请重启手机生效。"

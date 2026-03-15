#!/system/bin/sh

SKIPUNZIP=1

ui_print "****************************************"
ui_print "*      SMS Forwarder to PushPlus       *"
ui_print "*           Module v1.0.0              *"
ui_print "****************************************"

ui_print "- Extracting module files..."
unzip -o "$ZIPFILE" 'module.prop' -d "$MODPATH" >&2
unzip -o "$ZIPFILE" 'service.sh' -d "$MODPATH" >&2
unzip -o "$ZIPFILE" 'system/*' -d "$MODPATH" >&2

# Set permissions
ui_print "- Setting permissions..."
set_perm_recursive "$MODPATH" 0 0 0755 0644
set_perm "$MODPATH/service.sh" 0 0 0755
set_perm_recursive "$MODPATH/system/priv-app/SmsForwarder" 0 0 0755 0644

ui_print "- Installation complete. Please reboot."

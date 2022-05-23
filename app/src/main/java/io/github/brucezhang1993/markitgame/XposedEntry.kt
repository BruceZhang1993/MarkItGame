package io.github.brucezhang1993.markitgame

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Build
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage

class XposedEntry : IXposedHookZygoteInit, IXposedHookLoadPackage {
    companion object {
        fun handleApplicationInfo(applicationInfo: ApplicationInfo) {
            if (arrayListOf("com.llfz.bilibili", "com.leiting.wf.bilibili").contains(applicationInfo.packageName)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    applicationInfo.category = ApplicationInfo.CATEGORY_GAME
                }
                applicationInfo.flags = applicationInfo.flags or ApplicationInfo.FLAG_IS_GAME
            }
        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        EzXHelperInit.initZygote(startupParam)
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedHelpers.findAndHookMethod(
            "android.app.ApplicationPackageManager",
            lpparam.classLoader,
            "getInstalledApplicationsAsUser",
            Int::class.javaPrimitiveType,
            Int::class.javaPrimitiveType,
            ApplicationListHook()
        )
        XposedHelpers.findAndHookMethod(
            "com.android.server.pm.PackageManagerService",
            lpparam.classLoader,
            "getInstalledApplicationsListInternal",
            Int::class.javaPrimitiveType,
            Int::class.javaPrimitiveType,
            Int::class.javaPrimitiveType,
            ApplicationListHook()
        )
        XposedHelpers.findAndHookMethod(
            "android.app.ApplicationPackageManager",
            lpparam.classLoader,
            "getApplicationInfoAsUser",
            String::class.java,
            Int::class.javaPrimitiveType,
            Int::class.javaPrimitiveType,
            ApplicationInfoHook()
        )
        XposedHelpers.findAndHookMethod(
            "com.android.server.pm.PackageManagerService",
            lpparam.classLoader,
            "getApplicationInfo",
            String::class.java,
            Int::class.javaPrimitiveType,
            Int::class.javaPrimitiveType,
            ApplicationInfoHook()
        )
    }
    class ApplicationListHook: XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            try {
                @Suppress("UNCHECKED_CAST")
                val applicationInfos: List<ApplicationInfo> = param?.resultOrThrowable as List<ApplicationInfo>
                applicationInfos.forEach { applicationInfo ->
                    handleApplicationInfo(applicationInfo)
                }
                param.result = applicationInfos
            } catch (_: NameNotFoundException) {
            } catch (_: Throwable) {
            }
        }
    }
    class ApplicationInfoHook: XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            try {
                val applicationInfo: ApplicationInfo = param?.resultOrThrowable as ApplicationInfo
                handleApplicationInfo(applicationInfo)
                param.result = applicationInfo
            } catch (_: NameNotFoundException) {
            } catch (_: Throwable) {
            }
        }
    }
}

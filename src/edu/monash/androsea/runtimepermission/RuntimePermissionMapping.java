package edu.monash.androsea.runtimepermission;

import java.util.HashSet;
import java.util.Set;

import edu.monash.androsea.JavaFileExtractor;
import edu.monash.androsea.JavaFileMethodExtractor;
import edu.monash.androsea.MethodSig;

/**
 * 
 * RequiresPermission Annotation
 * https://developer.android.com/reference/androidx/annotation/RequiresPermission
 * 
 * Permission Group
 * https://developer.android.com/reference/android/Manifest.permission_group
 * 
 * Permission List
 * https://developer.android.com/reference/android/Manifest.permission
 * 
 * @author clii0040
 *
 */
public class RuntimePermissionMapping {

	public static void main(String[] args) 
	{
		String repoPath = args[0];

		Set<String> javaFiles = JavaFileExtractor.extractJavaFiles(repoPath);
		
		Set<String> apis = new HashSet<String>();
		Set<String> permissions = new HashSet<String>();
		
		for (String javaFilePath : javaFiles) 
		{
			Set<MethodSig> methodSigs = JavaFileMethodExtractor.extract(repoPath + javaFilePath);
			for (MethodSig mSig : methodSigs)
			{
				if (! (mSig.isPrivate || mSig.isInternal || mSig.isHidden) )
				{
					if (! mSig.annotations.isEmpty())
					{
						String value = mSig.annotations.get("RequiresPermission");
						//System.out.println(value);
						
						value = value.replace("android.Manifest.permission.", "")
								.replace("Manifest.permission.", "")
								.replace("{", "").replace("}", "").replace("(", "").replace(")", "").trim();
						
						String item = mSig.getSignature();
						
						if (value.contains("="))
						{
							value = value.split("=")[1].trim();
						}
						
						if (value.contains(","))
						{
							String[] perms = value.split(",");
							for (String perm : perms)
							{
								permissions.add(perm.trim());
								item = item + "|android.permission." + perm.trim();
							}
						}
						else
						{
							permissions.add(value);
							item = item + "|android.permission." + value;
						}
						
						apis.add(item);
					}
				}
			}
		}
		
		//apis.forEach(n -> System.out.println(n));
		
		System.out.println(permissions.size() + "," + apis.size());
	}

}

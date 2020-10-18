package edu.monash.androsea;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;


public class JavaSourceVisitor
{
	public static Set<MethodSig> visit(String javaFilePath) throws FileNotFoundException, ParseException 
	{
		FileInputStream in = new FileInputStream(javaFilePath);
		
		CompilationUnit cu = null;
		cu = JavaParser.parse(in);
		
		//Default Pkg Name
		String pkgName = "";
		
		Optional<PackageDeclaration> optionalPD = cu.getPackageDeclaration();
		if (optionalPD.isPresent())
		{
			pkgName = optionalPD.get().getNameAsString();
		}
		
		ClassOrInterfaceVisitor visitor = new ClassOrInterfaceVisitor(pkgName);
		visitor.visit(cu, null);
		
		return visitor.getMethodSigSet();
	}
}

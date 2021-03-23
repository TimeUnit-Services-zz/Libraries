package rip.lazze.libraries.utilities.commands.parameter.filter;

import java.util.regex.Pattern;

public class NormalFilter extends BaseFilter {
    public NormalFilter() {
        this.bannedPatterns.add(Pattern.compile("n+[i1l|]+gg+[e3]+r+", 2));
        this.bannedPatterns.add(Pattern.compile("k+i+l+l+ *y*o*u+r+ *s+e+l+f+", 2));
        this.bannedPatterns.add(Pattern.compile("f+a+g+[o0]+t+", 2));
        this.bannedPatterns.add(Pattern.compile("\\bk+y+s+\\b", 2));
        this.bannedPatterns.add(Pattern.compile("b+e+a+n+e+r+", 2));
        this.bannedPatterns.add(Pattern.compile("\\d{1,3}[,.]\\d{1,3}[,.]\\d{1,3}[,.]\\d{1,3}", 2));
        this.bannedPatterns.add(Pattern.compile("optifine\\.(?=\\w+)(?!net)", 2));
        this.bannedPatterns.add(Pattern.compile("gyazo\\.(?=\\w+)(?!com)", 2));
        this.bannedPatterns.add(Pattern.compile("prntscr\\.(?=\\w+)(?!com)", 2));
    }
}
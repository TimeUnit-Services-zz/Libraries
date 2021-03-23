package rip.lazze.libraries.utilities.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

public class ArgumentProcessor implements Processor<String[], Arguments> {
    public ArgumentProcessor() {
    }

    public Arguments process(String[] value) {
        Set<String> flags = new HashSet();
        List<String> arguments = new ArrayList();
        String[] var4 = value;
        int var5 = value.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            String s = var4[var6];
            if (!s.isEmpty()) {
                if (s.charAt(0) == '-' && !s.equals("-") && this.matches(s)) {
                    String flag = this.getFlagName(s);
                    flags.add(flag);
                } else {
                    arguments.add(s);
                }
            }
        }

        return new Arguments(arguments, flags);
    }

    private String getFlagName(String flag) {
        Matcher matcher = Flag.FLAG_PATTERN.matcher(flag);
        if (matcher.matches()) {
            String name = matcher.replaceAll("$2$3");
            return name.length() == 1 ? name : name.toLowerCase();
        } else {
            return null;
        }
    }

    private boolean matches(String flag) {
        return Flag.FLAG_PATTERN.matcher(flag).matches();
    }
}

package com.jd.blockchain.utils.decompiler.loads;

import com.strobel.assembler.ir.ConstantPool;
import com.strobel.assembler.metadata.Buffer;
import com.strobel.assembler.metadata.ClasspathTypeLoader;
import com.strobel.assembler.metadata.ITypeLoader;
import com.strobel.core.StringUtilities;
import com.strobel.core.VerifyArgument;

import java.io.ByteArrayInputStream;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BytesTypeLoader implements ITypeLoader {

    private final static Logger LOG = Logger.getLogger(BytesTypeLoader.class.getSimpleName());

    private final ITypeLoader defaultTypeLoader;
    private final Map<String, LinkedHashSet<byte[]>> packageLocations;
    private final Map<String, byte[]> knownBytes;

    private String name;

    public BytesTypeLoader(byte[] bytes) {
        this(new ClasspathTypeLoader(), bytes);
    }

    public BytesTypeLoader(final ITypeLoader defaultTypeLoader, byte[] bytes) {
        this.defaultTypeLoader = VerifyArgument.notNull(defaultTypeLoader, "defaultTypeLoader");
        this.packageLocations = new LinkedHashMap<>();
        this.knownBytes = new LinkedHashMap<>();
        Buffer innerNameBuffer = new Buffer();
        if (tryLoadTypeFromBytes(bytes, innerNameBuffer)) {
            this.name = getInternalNameFromClassFile(innerNameBuffer);
            this.knownBytes.put(this.name, bytes);
        } else {
            throw new IllegalStateException("Input Class Bytes Exception !!!");
        }
    }

    @Override
    public boolean tryLoadType(final String typeNameOrPath, final Buffer buffer) {
        VerifyArgument.notNull(typeNameOrPath, "typeNameOrPath");
        VerifyArgument.notNull(buffer, "buffer");

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Attempting to load type: " + typeNameOrPath + "...");
        }

        final boolean hasExtension = StringUtilities.endsWithIgnoreCase(typeNameOrPath, ".class");

        if (hasExtension) {
            return false;
        }

        String internalName = typeNameOrPath;

        if (tryLoadTypeFromName(internalName, buffer)) {
            return true;
        }

        for (int lastDelimiter = internalName.lastIndexOf('/');
             lastDelimiter != -1;
             lastDelimiter = internalName.lastIndexOf('/')) {

            internalName = internalName.substring(0, lastDelimiter) + "$" +
                    internalName.substring(lastDelimiter + 1);

            if (tryLoadTypeFromName(internalName, buffer)) {
                return true;
            }
        }

        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("Failed to load type: " + typeNameOrPath + ".");
        }

        return false;
    }

    private boolean tryLoadTypeFromName(final String internalName, final Buffer buffer) {
        if (tryLoadFromKnownLocation(internalName, buffer)) {
            return true;
        }

        if (defaultTypeLoader.tryLoadType(internalName, buffer)) {
            return true;
        }

        return false;
    }

    private boolean tryLoadFromKnownLocation(final String internalName, final Buffer buffer) {
        final byte[] knownFile = knownBytes.get(internalName);

        if (tryLoadBytes(knownFile, buffer)) {
            return true;
        }

        final int packageEnd = internalName.lastIndexOf('/');

        String head;
        String tail;

        if (packageEnd < 0 || packageEnd >= internalName.length()) {
            head = StringUtilities.EMPTY;
            tail = internalName;
        }
        else {
            head = internalName.substring(0, packageEnd);
            tail = internalName.substring(packageEnd + 1);
        }

        while (true) {
            final LinkedHashSet<byte[]> directories = packageLocations.get(head);

            if (directories != null) {
                for (final byte[] directory : directories) {
                    if (tryLoadBytes(internalName, directory, buffer, true)) {
                        return true;
                    }
                }
            }

            final int split = head.lastIndexOf('/');

            if (split <= 0) {
                break;
            }

            tail = head.substring(split + 1) + '/' + tail;
            head = head.substring(0, split);
        }

        return false;
    }

    private boolean tryLoadBytes(final byte[] bytes, final Buffer buffer) {

        if (bytes == null || bytes.length == 0) {
            return false;
        }

        int length = bytes.length;
        buffer.position(0);
        buffer.reset(length);
        new ByteArrayInputStream(bytes).read(buffer.array(), 0, length);
        buffer.position(0);

        return true;
    }

    private boolean tryLoadBytes(final String internalName, final byte[] bytes, final Buffer buffer, final boolean trustName) {
        if (!tryLoadBytes(bytes, buffer)) {
            return false;
        }

        final String actualName = getInternalNameFromClassFile(buffer);

        final String name = trustName ? (internalName != null ? internalName : actualName)
                : actualName;

        if (name == null) {
            return false;
        }

        final boolean nameMatches = StringUtilities.equals(actualName, internalName);

        final boolean result = internalName == null || nameMatches;

        if (result) {
            final int packageEnd = name.lastIndexOf('/');
            final String packageName;

            if (packageEnd < 0 || packageEnd >= name.length()) {
                packageName = StringUtilities.EMPTY;
            }
            else {
                packageName = name.substring(0, packageEnd);
            }

            registerKnownPath(packageName, bytes);

            knownBytes.put(actualName, bytes);

        }
        else {
            buffer.reset(0);
        }

        return result;
    }

    private void registerKnownPath(final String packageName, final byte[] directory) {
        if (directory == null || directory.length == 0) {
            return;
        }

        LinkedHashSet<byte[]> directories = packageLocations.get(packageName);

        if (directories == null) {
            packageLocations.put(packageName, directories = new LinkedHashSet<>());
        }

        if (!directories.add(directory)) {
            return;
        }
    }

    private static String getInternalNameFromClassFile(final Buffer b) {
        final long magic = b.readInt() & 0xFFFFFFFFL;

        if (magic != 0xCAFEBABEL) {
            return null;
        }

        b.readUnsignedShort(); // minor version
        b.readUnsignedShort(); // major version

        final ConstantPool constantPool = ConstantPool.read(b);

        b.readUnsignedShort(); // access flags

        final ConstantPool.TypeInfoEntry thisClass = constantPool.getEntry(b.readUnsignedShort());

        b.position(0);

        return thisClass.getName();
    }

    public String getName() {
        return name;
    }

    private boolean tryLoadTypeFromBytes(byte[] bytes, Buffer buffer) {
        if (bytes == null || bytes.length == 0 || buffer == null) {
            return false;
        }
        int length = bytes.length;
        buffer.position(0);
        buffer.reset(length);
        new ByteArrayInputStream(bytes).read(buffer.array(), 0, length);
        buffer.position(0);
        return true;
    }
}

package com.treetank.access;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.treetank.exception.TreetankException;
import com.treetank.exception.TreetankIOException;
import com.treetank.exception.TreetankUsageException;
import com.treetank.settings.EDatabaseSetting;
import com.treetank.settings.EStoragePaths;

/**
 * <h1>Database Configuration</h1> This class represents a configuration of a
 * database. This includes all settings which have to be made when it comes to
 * the creation of the database.
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public class DatabaseConfiguration {

    /** Absolute path to tnk directory. */
    private final File mFile;

    /** Props to hold all related data */
    private final Properties mProps;

    /**
     * Constructor, just the location of either an existing or a new database.
     * 
     * @param file
     *            the path to the database
     * @throws TreetankException
     *             if the reading of the props is failing or properties are not
     *             valid
     */
    public DatabaseConfiguration(final File file) throws TreetankException {
        this(file, EStoragePaths.DBSETTINGS.getFile());
    }

    /**
     * Constructor with all possible properties.
     * 
     * @param file
     *            the path to the database
     * @param props
     *            properties to be set for setting
     * @throws TreetankUsageException
     *             if properties are not valid
     */
    public DatabaseConfiguration(final File file, final Properties props)
            throws TreetankUsageException {
        mFile = file;
        mProps = new Properties();
        buildUpProperties(props);

    }

    /**
     * Constructor with all possible properties stored in a file
     * 
     * @param file
     *            the path to the database
     * @param propFile
     *            properties to be set
     * @throws TreetankException
     *             if the reading of the props is failing or properties are not
     *             valid
     */
    public DatabaseConfiguration(final File file, final File propFile)
            throws TreetankException {
        mFile = file;
        mProps = new Properties();
        final Properties loadProps = new Properties();
        try {
            loadProps.load(new FileInputStream(propFile));
        } catch (final IOException exc) {
            throw new TreetankIOException(exc);
        }
        buildUpProperties(loadProps);

        // Check if database was existing. If so, check against the checksum
        if ((propFile.getName().equals(
                EStoragePaths.DBSETTINGS.getFile().getName()) && propFile
                .getParentFile().equals(file))
                && !getProps().getProperty(EDatabaseSetting.CHECKSUM.name())
                        .equals(Integer.toString(this.hashCode()))) {
            throw new TreetankUsageException(new StringBuilder(
                    "Checksums differ: Loaded ").append(getProps().toString())
                    .append(" and expected ").append(this.toString())
                    .toString());

        }

    }

    /**
     * Building up the properties and replacing all missing with the values from
     * the standard one.
     * 
     * @param props
     *            to be included
     * @throws TreetankUsageException
     *             if wrong properties are into existing database
     */
    private void buildUpProperties(final Properties props)
            throws TreetankUsageException {
        for (final EDatabaseSetting enumProps : EDatabaseSetting.values()) {
            if (props.containsKey(enumProps.name())) {
                this.getProps().setProperty(enumProps.name(),
                        props.getProperty(enumProps.name()));
            } else {
                this.getProps().setProperty(enumProps.name(),
                        enumProps.getStandardProperty());
            }
        }

    }

    /**
     * Getter for the properties. The values are refered over
     * {@link EDatabaseSetting}.
     * 
     * @return the properties
     */
    public Properties getProps() {
        return mProps;
    }

    /**
     * Get tnk folder.
     * 
     * @return Path to tnk folder.
     */
    public File getFile() {
        return mFile;
    }

    /**
     * Serializing the data
     */
    public void serialize() throws TreetankIOException {
        try {
            final Integer hashCode = this.hashCode();
            getProps().setProperty("checksum", Integer.toString(hashCode));

            getProps().store(
                    new FileOutputStream(EStoragePaths.DBSETTINGS.getFile()),
                    "");
        } catch (final IOException exc) {
            throw new TreetankIOException(exc);
        }
    }
}
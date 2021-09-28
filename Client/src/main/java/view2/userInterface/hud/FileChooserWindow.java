package view2.userInterface.hud;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3FileHandle;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view2.Chati;
import view2.userInterface.ChatiLabel;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.ChatiTextField;
import view2.userInterface.ChatiWindow;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileFilter;

/**
 * Eine Klasse, welche ein Fenster zum Auswählen einer Datei repräsentiert.
 */
public abstract class FileChooserWindow extends ChatiWindow {

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 675;
    private static final int MIN_TAP_COUNT = 2;
    private static final float TAP_COUNT_INTERVAL = 0.5f;

    private final FileFilter fileFilter;
    private final Label currentDirectoryLabel;
    private final List<FileListItem> fileList;
    private final String fileName;

    private FileHandle currentDirectory;

    /**
     * Erzeugt eine neue Instanz des FileChooserWindow.
     * @param titleKey Kennung des Titels.
     * @param fileFilter Filter der anzuzeigenden Dateien.
     * @param save Falls true, wird ein Fenster zum Speichern einer Datei geöffnet, sonst zum Laden einer Datei.
     * @param fileName Vorgegebener Name zum Speichern einer Datei oder null, bei einem Fenster zum Laden von Dateien.
     */
    protected FileChooserWindow(@NotNull final String titleKey, @NotNull final FileFilter fileFilter, final boolean save,
                                @Nullable final String fileName) {
        super(titleKey, WINDOW_WIDTH, WINDOW_HEIGHT);
        this.fileFilter = fileFilter;
        this.fileName = fileName;

        if (save) {
            infoLabel = new ChatiLabel("window.entry.save-file");
        } else {
            infoLabel = new ChatiLabel("window.entry.choose-file");
        }

        currentDirectoryLabel = new Label("", Chati.CHATI.getSkin());
        currentDirectoryLabel.setWrap(true);

        this.fileList = new List<>(Chati.CHATI.getSkin());
        ClickListener fileClickListener= new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                if (getTapCount() >= MIN_TAP_COUNT) {
                    setTapCount(0);
                    if (fileList.getSelected() != null && fileList.getSelected().getFileHandle().isDirectory()) {
                        changeDirectory(fileList.getSelected().getFileHandle());
                    }
                }
            }
        };
        fileClickListener.setTapCountInterval(TAP_COUNT_INTERVAL);
        fileList.addListener(fileClickListener);

        ScrollPane fileListScrollPane = new ScrollPane(fileList, Chati.CHATI.getSkin());
        fileListScrollPane.setFadeScrollBars(false);
        fileListScrollPane.setOverscroll(false, false);
        fileListScrollPane.setScrollingDisabled(true, false);

        String homePath = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
        changeDirectory(Gdx.files.absolute(homePath));

        // Layout
        setModal(true);
        setMovable(false);

        Table container = new Table();
        container.defaults().height(ROW_HEIGHT).padBottom(SPACE).center().growX();
        infoLabel.setAlignment(Align.center, Align.center);
        container.add(infoLabel).padTop(SPACE).row();

        currentDirectoryLabel.setAlignment(Align.left, Align.left);
        container.add(currentDirectoryLabel).row();

        container.add(fileListScrollPane).height(0).grow().row();

        if (save) {
            layoutSaveWindow(container);
        } else {
            layoutLoadWindow(container);
        }

        translates.trimToSize();
        add(container).padLeft(SPACE).padRight(SPACE).grow();
    }

    /**
     * Setzt das Layout des Fensters zum Speichern von Dateien.
     * @param container Container, in dem das Layout gesetzt wird.
     */
    private void layoutSaveWindow(@NotNull final Table container) {
        ChatiTextButton saveButton = new ChatiTextButton("menu.button.save-file", true);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                new ConfirmWindow("window.title.save-file", true).open();
            }
        });

        ChatiTextButton newDirectoryButton = new ChatiTextButton("menu.button.new-directory", true);
        newDirectoryButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                new ConfirmWindow("window.title.new-directory", false).open();
            }
        });

        ChatiTextButton cancelButton = new ChatiTextButton("menu.button.cancel", true);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                close();
            }
        });

        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
        buttonContainer.add(saveButton).padRight(SPACE / 2);
        buttonContainer.add(newDirectoryButton).padLeft(SPACE / 2).padRight(SPACE / 2);
        buttonContainer.add(cancelButton).padLeft(SPACE / 2);

        container.add(buttonContainer).row();

        // Translatable Register
        translates.add(saveButton);
        translates.add(newDirectoryButton);
        translates.add(cancelButton);
    }

    /**
     * Setzt das Layout des Fensters zum Laden von Dateien.
     * @param container Container, in dem das Layout gesetzt wird.
     */
    private void layoutLoadWindow(@NotNull final Table container) {
        ChatiTextButton chooseButton = new ChatiTextButton("menu.button.choose", true);
        chooseButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                if (fileList.getSelected() == null) {
                    return;
                }
                if (fileList.getSelected().getFileHandle().isDirectory()) {
                    changeDirectory(fileList.getSelected().getFileHandle());
                } else {
                    if (loadFile(fileList.getSelected().getFileHandle())) {
                        close();
                    }
                }
            }
        });

        ChatiTextButton cancelButton = new ChatiTextButton("menu.button.cancel", true);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                close();
            }
        });

        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
        buttonContainer.add(chooseButton).padRight(SPACE / 2);
        buttonContainer.add(cancelButton).padLeft(SPACE / 2);

        container.add(buttonContainer).row();

        // Translatable Register
        translates.add(chooseButton);
        translates.add(cancelButton);
    }

    @Override
    public void focus() {
        super.focus();
        if (getStage() != null) {
            getStage().setScrollFocus(fileList);
        }
    }

    /**
     * Lädt eine Datei.
     * @param fileHandle Datei, die geladen werden soll.
     * @return true, falls die Datei erfolgreich geladen wurde, sonst false.
     */
    protected abstract boolean loadFile(@NotNull final FileHandle fileHandle);

    /**
     * Speichert eine Datei.
     * @param fileHandle Datei, die gespeichert werden soll.
     * @return true, falls die Datei erfolgreich gespeichert wurde, sonst false.
     */
    protected abstract boolean saveFile(@NotNull final FileHandle fileHandle);

    /**
     * Wechselt den angezeigten Ordner.
     * @param directoryHandle Datei des neu anzuzeigenden Ordners.
     */
    private void changeDirectory(@NotNull final FileHandle directoryHandle) {
        if (!directoryHandle.isDirectory()) {
            return;
        }
        currentDirectory = directoryHandle;

        Array<FileListItem> currentFileListItems = new Array<>();
        if (directoryHandle.path().isBlank() || directoryHandle.path().equals("/")) {
            /*
             * Ist der Pfadname leer oder besteht nur aus einem Slash, so handelt es sich um das UNIX Root Directory,
             * das von LibGDX gesetzt wurde. Da dies unter Windows-Systemen zu Fehlern führt, wird dieser Fall extra
             * gehandhabt.
             */
            currentDirectoryLabel.setText(Chati.CHATI.getLocalization().translate("window.file-chooser.root"));
            for (File drive : File.listRoots()) {
                currentFileListItems.add(new FileListItem(new Lwjgl3FileHandle(drive, FileType.Absolute), false));
            }
        } else {
            currentDirectoryLabel.setText(directoryHandle.file().getAbsolutePath());

            FileHandle[] currentFileHandles =
                    directoryHandle.list(file -> (file.isDirectory() || fileFilter.accept(file)) && !file.isHidden());

            for (FileHandle fileHandle : currentFileHandles) {
                currentFileListItems.add(new FileListItem(fileHandle, false));
            }
            if (directoryHandle.parent() != null) {
                currentFileListItems.add(new FileListItem(directoryHandle.parent(), true));
            }
        }
        currentFileListItems.sort();

        fileList.setItems(currentFileListItems);
        fileList.setSelected(null);
    }

    /**
     * Eine Klasse, die einen Eintrag in der Liste der Dateien repräsentiert.
     */
    private static class FileListItem implements Comparable<FileListItem> {

        private final FileHandle fileHandle;
        private final boolean parent;

        /**
         * Erzeugt eine neue Instanz eines FileListItem.
         * @param fileHandle Zugehörige Datei.
         * @param parent Gibt an, ob die Datei der übergeordnete Ordner des gerade angezeigten Ordners ist.
         */
        public FileListItem(@NotNull final FileHandle fileHandle, final boolean parent) {
            this.fileHandle = fileHandle;
            this.parent = parent;
        }

        /**
         * Gibt die Datei des Listeneintrags zurück.
         * @return Datei
         */
        public FileHandle getFileHandle() {
            return fileHandle;
        }

        @Override
        public int compareTo(@NotNull final FileListItem other) {
            int parentComp = Boolean.compare(other.parent, this.parent);
            int directoryComp = Boolean.compare(other.fileHandle.isDirectory(), this.fileHandle.isDirectory());
            int nameComp = this.fileHandle.toString().compareTo(other.fileHandle.toString());
            return parentComp != 0 ? parentComp : (directoryComp != 0 ? directoryComp : nameComp);
        }

        @Override
        public String toString() {
            return parent ? ".." : fileHandle.isDirectory() ? (fileHandle.name().isBlank()
                    ? FileSystemView.getFileSystemView().getSystemDisplayName(fileHandle.file())
                    : fileHandle.name().concat("/")) : fileHandle.name();
        }
    }

    /**
     * Eine Klasse, welches ein Fenster zum Erzeugen eines neuen Ordners repräsentiert.
     */
    private class ConfirmWindow extends ChatiWindow {

        private static final float WINDOW_WIDTH = 550;
        private static final float WINDOW_HEIGHT = 325;

        private final ChatiTextField nameField;

        /**
         * Erzeugt eine Instanz eines NewDirectoryWindow.
         * @param titleKey Kennung des Titels.
         * @param save Falls true, wird ein Bestätigungsfenster zum Speichern von Dateien geöffnet, sonst zum Erstellen
         * eines neuen Ordners.
         */
        protected ConfirmWindow(@NotNull final String titleKey, final boolean save) {
            super(titleKey, WINDOW_WIDTH, WINDOW_HEIGHT);

            ChatiTextButton confirmButton = new ChatiTextButton("menu.button.confirm", true);
            if (save) {
                infoLabel = new ChatiLabel("window.entry.enter-filename");
                nameField = new ChatiTextField("menu.text-field.filename", ChatiTextField.TextFieldType.FILE);
                if (fileName != null) {
                    nameField.setText(fileName);
                    nameField.setCursorPosition(fileName.length());
                }
                confirmButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                        if (currentDirectory == null || !currentDirectory.isDirectory()) {
                            return;
                        }
                        if (nameField.isBlank()) {
                            showMessage("window.entry.enter-filename");
                            return;
                        }
                        if (currentDirectory.child(nameField.getText()).exists()) {
                            showMessage("window.file-chooser.file-already-exists");
                            return;
                        }
                        FileHandle newFile = Gdx.files.absolute(currentDirectory.path().concat("/").concat(nameField.getText().trim()));
                        if (!fileFilter.accept(newFile.file())) {
                            infoLabel.setText(Chati.CHATI.getLocalization().translate(""));
                        }
                        if (saveFile(newFile)) {
                            close();
                            FileChooserWindow.this.showMessage("window.file-chooser.save-success");
                        } else {
                            close();
                            FileChooserWindow.this.showMessage("window.file-chooser.save-failed");
                        }
                    }
                });
            } else {
                infoLabel = new ChatiLabel("window.entry.enter-directory");
                nameField = new ChatiTextField("menu.text-field.directory", ChatiTextField.TextFieldType.FILE);
                confirmButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                        if (nameField.isBlank()) {
                            showMessage("window.entry.enter-directory");
                            return;
                        }
                        if (currentDirectory.child(nameField.getText()).exists()) {
                            showMessage("window.file-chooser.directory-already-exists");
                            return;
                        }
                        FileHandle newDirectory = Gdx.files.absolute(currentDirectory.path().concat("/").concat(nameField.getText().trim()));
                        newDirectory.mkdirs();
                        changeDirectory(currentDirectory);
                        close();
                        FileChooserWindow.this.showMessage("window.file-chooser.directory-created");
                    }
                });
            }
            ChatiTextButton cancelButton = new ChatiTextButton("menu.button.cancel", true);
            cancelButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                    close();
                }
            });

            // Layout
            setModal(true);
            setMovable(false);

            Table container = new Table();
            container.defaults().height(ROW_HEIGHT).spaceBottom(SPACE).center().growX();
            infoLabel.setAlignment(Align.center, Align.center);
            infoLabel.setWrap(true);
            container.add(infoLabel).spaceBottom(2 * SPACE).row();
            container.add(nameField).row();
            Table buttonContainer = new Table();
            buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
            buttonContainer.add(confirmButton).padRight(SPACE / 2);
            buttonContainer.add(cancelButton).padLeft(SPACE / 2);
            container.add(buttonContainer);
            add(container).padLeft(SPACE).padRight(SPACE).grow();

            // Translatable register
            translates.add(infoLabel);
            translates.add(confirmButton);
            translates.add(cancelButton);
            translates.trimToSize();
        }

        @Override
        public void focus() {
            super.focus();
            if (getStage() != null) {
                getStage().setKeyboardFocus(nameField);
            }
        }
    }
}

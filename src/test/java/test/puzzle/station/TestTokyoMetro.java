package test.puzzle.station;

import static java.util.stream.Collectors.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.junit.Test;

public class TestTokyoMetro {

    static final Logger logger = Logger.getLogger(TestTokyoMetro.class.getName());

    static final Charset CHARSET = StandardCharsets.UTF_8;
    static final Path DIRECTORY = Paths.get("data", "eki");
    static final Path LINE_CSV = DIRECTORY.resolve("line20200619free.csv");
    static final Path STATION_CSV = DIRECTORY.resolve("station20200619free.csv");
    static final Path JOIN_CSV = DIRECTORY.resolve("join20200619.csv");
    static final Path COLOR_CSV = DIRECTORY.resolve("metro_color.csv");
    static final Path GML = DIRECTORY.resolve("metro.gml");

    static List<List<String>> readCSV(Path file) throws IOException {
        return Files.readAllLines(file, CHARSET).stream()
            .map(line -> List.of(line.split(",")))
            .collect(toList());
    }

    @Test
    public void 最初のプログラム() throws IOException {
        // 東京の地下鉄の路線
        List<List<String>> lines = readCSV(LINE_CSV).stream()
            .filter(line -> line.get(2).startsWith("東京メトロ") || line.get(2).startsWith("都営"))
            .collect(toList());

        // 東京の地下鉄の路線コードのリスト
        List<String> lineCodes = lines.stream()
            .map(line -> line.get(0))
            .collect(toList());

        // 東京の地下鉄の駅
        List<List<String>> stations = readCSV(STATION_CSV).stream()
            .filter(station -> lineCodes.contains(station.get(5)))
            .collect(toList());

        // 東京の地下鉄の駅の接続
        List<List<String>> joins = readCSV(JOIN_CSV).stream()
            .filter(line -> lineCodes.contains(line.get(0)))
            .collect(toList());

        // グラフの作成
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(GML))) {
            w.println("graph [");
            for (List<String> s : stations) {
                w.println("  node [");
                w.println("    id " + s.get(0)); // 駅コード
                w.println("    label \"" + s.get(2) + "\""); // 駅名
                w.println("  ]");
            }
            for (List<String> j : joins) {
                w.println("  edge [");
                w.println("    source " + j.get(1)); // 接続駅コード1
                w.println("    target " + j.get(2)); // 接続駅コード2
                w.println("  ]");
            }
            w.println("]");
        }
    }

    @Test
    public void 市ケ谷の問題あり() throws IOException {
        // 東京の地下鉄の路線
        List<List<String>> lines = readCSV(LINE_CSV).stream()
            .filter(line -> line.get(2).startsWith("東京メトロ") || line.get(2).startsWith("都営"))
            .collect(toList());

        // 東京の地下鉄の路線コードのリスト
        List<String> lineCodes = lines.stream()
            .map(line -> line.get(0))
            .collect(toList());

        // 東京の地下鉄の駅
        List<List<String>> stations = readCSV(STATION_CSV).stream()
            .filter(station -> lineCodes.contains(station.get(5)))
            .collect(toList());

        // 駅名でグループ化した駅コード (ex. 新宿=[2800218, 9930128, 9930401])
        Map<String, List<String>> stationNameMap = stations.stream()
            .collect(groupingBy(e -> e.get(2),
                mapping(e -> e.get(0), toList())));

        // 駅コードから代表駅コードへのマップ (ex. 2800218=2800218, 9930128=2800218,
        // 9930401=2800218)
        Map<String, String> stationCodeMap = stationNameMap.values().stream()
            .flatMap(codes -> codes.stream().map(code -> Map.entry(code, codes.get(0))))
            .collect(toMap(Entry::getKey, Entry::getValue));

        // 東京の地下鉄の駅の接続
        List<List<String>> joins = readCSV(JOIN_CSV).stream()
            .filter(line -> lineCodes.contains(line.get(0)))
            .collect(toList());

        // グラフの作成
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(GML))) {
            w.println("graph [");
            for (Entry<String, List<String>> e : stationNameMap.entrySet()) { // 駅名で集約したデータを使用
                w.println("  node [");
                w.println("    id " + e.getValue().get(0)); // 代表駅コード
                w.println("    label \"" + e.getKey() + "\""); // 駅名
                w.println("  ]");
            }
            for (List<String> j : joins) {
                w.println("  edge [");
                w.println("    source " + stationCodeMap.get(j.get(1))); // 代表駅コードに変換
                w.println("    target " + stationCodeMap.get(j.get(2))); // 代表駅コードに変換
                w.println("  ]");
            }
            w.println("]");
        }
    }

    @Test
    public void 完成形() throws IOException {
        // 東京の地下鉄の路線
        List<List<String>> lines = readCSV(LINE_CSV).stream()
            .filter(line -> line.get(2).startsWith("東京メトロ") || line.get(2).startsWith("都営"))
            .collect(toList());

        // 東京の地下鉄の路線コードのリスト
        List<String> lineCodes = lines.stream()
            .map(line -> line.get(0))
            .collect(toList());

        // 東京の地下鉄の駅
        List<List<String>> stations = readCSV(STATION_CSV).stream()
            .filter(station -> lineCodes.contains(station.get(5)))
            .map(station -> station.stream()
                .map(item -> item.replace('ヶ', 'ケ')).collect(toList())) // 市ヶ谷と市ケ谷を統一
            .collect(toList());

        // 駅名でグループ化した駅コード (ex. 新宿=[2800218, 9930128, 9930401])
        Map<String, List<String>> stationNameMap = stations.stream()
            .collect(groupingBy(e -> e.get(2),
                mapping(e -> e.get(0), toList())));

        // 駅コードから代表駅コードへのマップ (ex. 2800218=2800218, 9930128=2800218,
        // 9930401=2800218)
        Map<String, String> stationCodeMap = stationNameMap.values().stream()
            .flatMap(codes -> codes.stream().map(code -> Map.entry(code, codes.get(0))))
            .collect(toMap(Entry::getKey, Entry::getValue));

        // 東京の地下鉄の駅の接続
        List<List<String>> joins = readCSV(JOIN_CSV).stream()
            .filter(line -> lineCodes.contains(line.get(0)))
            .collect(toList());

        // グラフの作成
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(GML))) {
            w.println("graph [");
            for (Entry<String, List<String>> e : stationNameMap.entrySet()) { // 駅名で集約したデータを使用
                w.println("  node [");
                w.println("    id " + e.getValue().get(0)); // 代表駅コード
                w.println("    label \"" + e.getKey() + "\""); // 駅名
                w.println("  ]");
            }
            for (List<String> j : joins) {
                w.println("  edge [");
                w.println("    source " + stationCodeMap.get(j.get(1))); // 代表駅コードに変換
                w.println("    target " + stationCodeMap.get(j.get(2))); // 代表駅コードに変換
                w.println("  ]");
            }
            w.println("]");
        }
    }

    @Test
    public void 色付き() throws IOException {
        // 東京の地下鉄の路線
        List<List<String>> lines = readCSV(LINE_CSV).stream()
            .filter(line -> line.get(2).startsWith("東京メトロ") || line.get(2).startsWith("都営"))
            .collect(toList());

        // 東京の地下鉄の路線色
        Map<String, String> colors = readCSV(COLOR_CSV).stream()
            .collect(toMap(line -> line.get(0), line -> line.get(1)));

        // 路線コード→路線色
        Map<String, String> lineColors = lines.stream()
            .collect(toMap(line -> line.get(0), line -> colors.get(line.get(2))));

        // 東京の地下鉄の路線コードのリスト
        List<String> lineCodes = lines.stream()
            .map(line -> line.get(0))
            .collect(toList());

        // 東京の地下鉄の駅
        List<List<String>> stations = readCSV(STATION_CSV).stream()
            .filter(station -> lineCodes.contains(station.get(5)))
            .map(station -> station.stream()
                .map(item -> item.replace('ヶ', 'ケ')).collect(toList())) // 市ヶ谷と市ケ谷を統一
            .collect(toList());

        // 駅コード→路線色
        Map<String, String> stationColors = stations.stream()
            .collect(toMap(line -> line.get(0), line -> lineColors.get(line.get(5))));

        // 駅名でグループ化した駅コード (ex. 新宿=[2800218, 9930128, 9930401])
        Map<String, List<String>> stationNameMap = stations.stream()
            .collect(groupingBy(e -> e.get(2),
                mapping(e -> e.get(0), toList())));

        // 駅コードから代表駅コードへのマップ (ex. 2800218=2800218, 9930128=2800218,
        // 9930401=2800218)
        Map<String, String> stationCodeMap = stationNameMap.values().stream()
            .flatMap(codes -> codes.stream().map(code -> Map.entry(code, codes.get(0))))
            .collect(toMap(Entry::getKey, Entry::getValue));

        // 東京の地下鉄の駅の接続
        List<List<String>> joins = readCSV(JOIN_CSV).stream()
            .filter(line -> lineCodes.contains(line.get(0)))
            .collect(toList());

        // グラフの作成
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(GML))) {
            w.println("graph [");
            for (Entry<String, List<String>> e : stationNameMap.entrySet()) { // 駅名で集約したデータを使用
                w.println("  node [");
                w.println("    id " + e.getValue().get(0)); // 代表駅コード
                w.println("    label \"" + e.getKey() + "\""); // 駅名
                w.println("    graphics [");
                if (stationNameMap.get(e.getKey()).size() > 1)
                    w.println("      fill \"#FFFFFF\"");    // 乗換駅は白に
                else
                    w.println("      fill \"" + stationColors.get(e.getValue().get(0)) + "\"");
                w.println("    ]");
                w.println("  ]");
            }
            for (List<String> j : joins) {
                w.println("  edge [");
                w.println("    source " + stationCodeMap.get(j.get(1))); // 代表駅コードに変換
                w.println("    target " + stationCodeMap.get(j.get(2))); // 代表駅コードに変換
                w.println("    graphics [");    // 矢印なしにする
                w.println("      sourceArrow \"none\"");
                w.println("      targetArrow \"none\"");
                w.println("    ]");
                w.println("  ]");
            }
            w.println("]");
        }
    }

}

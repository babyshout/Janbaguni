package kopo.poly.order.utill;

import kopo.poly.order.dto.CrawlingComposite;
import kopo.poly.order.dto.ProductCrawlingDTO;
import kopo.poly.order.dto.SearchCrawlingComposite;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class SortUtil {

    public List<ProductCrawlingDTO> sortProductList(List<ProductCrawlingDTO> list) {
        // 정렬할 때 쉼표를 제거하고 숫자로 비교
        Collections.sort(list, (p1, p2) -> {
            // 쉼표(,)를 제거하고 숫자로 변환
            int price1 = Integer.parseInt(p1.getPrice().replaceAll("[^0-9]", ""));
            int price2 = Integer.parseInt(p2.getPrice().replaceAll("[^0-9]", ""));
            return Integer.compare(price1, price2);
        });

        for (int i = 0; i < list.size(); i++) {
            log.info("sortProductList : " + list.get(i).getPrice());
        }

        return list;
    }

    // 다중 목록을 정렬하는 메서드
    public void sortCrawlingComposite(CrawlingComposite composite) {
        sortProductLists(composite.getAceList());
        sortProductLists(composite.getGoodFoodList());
        sortProductLists(composite.getBabyLeafList());
        sortProductLists(composite.getFoodEnList());
        sortProductLists(composite.getMonoMartList());
        // 다른 목록도 필요에 따라 추가 정렬
    }

//    public List<ProductCrawlingDTO> sortCheapestProudct(List<ProductCrawlingDTO> aceList, List<ProductCrawlingDTO>  goodFoodList,
//                                                        List<ProductCrawlingDTO> babyleafList, List<ProductCrawlingDTO>  foodEnList,
//                                                        List<ProductCrawlingDTO> monoMartList){
//
//        List<ProductCrawlingDTO> result = new ArrayList<>();
//
//
//
//
//
//        return sortProductList(result);
//    }

    // 다중 목록을 정렬하는 메서드
    public void sortProductLists(List<List<ProductCrawlingDTO>> productList) {
        for (List<ProductCrawlingDTO> list : productList) {
            sortProductList(list);
        }
    }


    // 단일 목록을 정렬하는 메서드
//    public List<ProductCrawlingDTO> sortProductList(List<ProductCrawlingDTO> list) {
//        Collections.sort(list, Comparator.comparing(ProductCrawlingDTO::getPrice));
//        return list;
//    }
//
//    // 다중 목록을 정렬하는 메서드
//    public void sortCrawlingComposite(CrawlingComposite composite) {
//        sortProductLists(composite.getAceList());
//        sortProductLists(composite.getGoodFoodList());
//        sortProductLists(composite.getBabyLeafList());
//        // 다른 목록도 필요에 따라 추가 정렬
//    }
//
//    // 다중 목록을 정렬하는 메서드
//    private void sortProductLists(List<List<ProductCrawlingDTO>> productList) {
//        for (List<ProductCrawlingDTO> list : productList) {
//            Collections.sort(list, Comparator.comparing(ProductCrawlingDTO::getPrice));
//        }
//    }

    public void sort(int[] a) {
        l_pivot_sort(a, 0, a.length - 1);
    }

    /**
     *  왼쪽 피벗 선택 방식
     * @param a		정렬할 배열
     * @param lo	현재 부분배열의 왼쪽
     * @param hi	현재 부분배열의 오른쪽
     */
    private void l_pivot_sort(int[] a, int lo, int hi) {

        /*
         *  lo가 hi보다 크거나 같다면 정렬 할 원소가
         *  1개 이하이므로 정렬하지 않고 return한다.
         */
        if(lo >= hi) {
            return;
        }

        /*
         * 피벗을 기준으로 요소들이 왼쪽과 오른쪽으로 약하게 정렬 된 상태로
         * 만들어 준 뒤, 최종적으로 pivot의 위치를 얻는다.
         *
         * 그리고나서 해당 피벗을 기준으로 왼쪽 부분리스트와 오른쪽 부분리스트로 나누어
         * 분할 정복을 해준다.
         *
         * [과정]
         *
         * Partitioning:
         *
         *   a[left]          left part              right part
         * +---------------------------------------------------------+
         * |  pivot  |    element <= pivot    |    element > pivot   |
         * +---------------------------------------------------------+
         *
         *
         *  result After Partitioning:
         *
         *         left part          a[lo]          right part
         * +---------------------------------------------------------+
         * |   element <= pivot    |  pivot  |    element > pivot    |
         * +---------------------------------------------------------+
         *
         *
         *  result : pivot = lo
         *
         *
         *  Recursion:
         *
         * l_pivot_sort(a, lo, pivot - 1)     l_pivot_sort(a, pivot + 1, hi)
         *
         *         left part                           right part
         * +-----------------------+             +-----------------------+
         * |   element <= pivot    |    pivot    |    element > pivot    |
         * +-----------------------+             +-----------------------+
         * lo                pivot - 1        pivot + 1                 hi
         *
         */
        int pivot = partition(a, lo, hi);

        l_pivot_sort(a, lo, pivot - 1);
        l_pivot_sort(a, pivot + 1, hi);
    }



    /**
     * pivot을 기준으로 파티션을 나누기 위한 약한 정렬 메소드
     *
     * @param a		정렬 할 배열
     * @param left	현재 배열의 가장 왼쪽 부분
     * @param right	현재 배열의 가장 오른쪽 부분
     * @return		최종적으로 위치한 피벗의 위치(lo)를 반환
     */
    private int partition(int[] a, int left, int right) {

        int lo = left;
        int hi = right;
        int pivot = a[left];		// 부분리스트의 왼쪽 요소를 피벗으로 설정

        // lo가 hi보다 작을 때 까지만 반복한다.
        while(lo < hi) {

            /*
             * hi가 lo보다 크면서, hi의 요소가 pivot보다 작거나 같은 원소를
             * 찾을 떄 까지 hi를 감소시킨다.
             */
            while(a[hi] > pivot && lo < hi) {
                hi--;
            }

            /*
             * hi가 lo보다 크면서, lo의 요소가 pivot보다 큰 원소를
             * 찾을 떄 까지 lo를 증가시킨다.
             */
            while(a[lo] <= pivot && lo < hi) {
                lo++;
            }

            // 교환 될 두 요소를 찾았으면 두 요소를 바꾼다.
            swap(a, lo, hi);
        }


        /*
         *  마지막으로 맨 처음 pivot으로 설정했던 위치(a[left])의 원소와
         *  lo가 가리키는 원소를 바꾼다.
         */
        swap(a, left, lo);

        // 두 요소가 교환되었다면 피벗이었던 요소는 lo에 위치하므로 lo를 반환한다.
        return lo;
    }

    private void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
}
